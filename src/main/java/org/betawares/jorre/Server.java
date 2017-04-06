/*
 * ISC License
 *
 * Copyright (c) 2016, Betawares
 *
 * Permission to use, copy, modify, and/or distribute this software for any 
 * purpose with or without fee is hereby granted, provided that the above 
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, 
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM 
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR 
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR 
 * PERFORMANCE OF THIS SOFTWARE.
 */

package org.betawares.jorre;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.betawares.jorre.handlers.PingMessageHandler;
import org.betawares.jorre.handlers.PongMessageHandler;
import org.betawares.jorre.handlers.server.ServerHeartbeatHandler;
import org.betawares.jorre.handlers.server.ServerExceptionHandler;
import org.betawares.jorre.handlers.server.ServerRequestHandler;
import org.betawares.jorre.messages.callback.ClientCallback;

/**
 * Abstract base class for server implementations.
 * 
 * A Server will listen on a specified port for incoming connections from a {@link Client}
 *
 * Overriding classes should add methods for application specific features.
 * 
 */
public abstract class Server implements ServerInterface {

    protected static final Logger logger = Logger.getLogger(Server.class);

    private static final int DEFAULT_IDLE_PING_TIME = 30;
    private static final int DEFAULT_IDLE_TIMEOUT = 60;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Map<UUID, Channel> clientIdToChannel = new ConcurrentHashMap<>();

    private final EventExecutorGroup handlersExecutor = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2);
    
    // sharable handlers - these handlers will be shared by all client connections
    private final ServerRequestHandler serverRequestHandler;
    private final ServerExceptionHandler exceptionHandler;
    private final PingMessageHandler pingMessageHandler = new PingMessageHandler();
    private final PongMessageHandler pongMessageHandler = new PongMessageHandler();
    private final ObjectEncoder encoder = new ObjectEncoder();
    
    private final Version version;

    /**
     * Creates a new <code>Server</code> with the specified {@link Version}
     * 
     * @param version the {@link Version} of the {@link Server} implementation
     */
    public Server(Version version) {
        this.version = version;
        serverRequestHandler = new ServerRequestHandler(this);
        exceptionHandler = new ServerExceptionHandler(this);
    }

    @Override
    public Version version() {
        return version;
    }
    
    /**
     * Starts the Server with the specified {@link Connection} settings.
     * 
     * @param connection  a {@link Connection} instance specifying the connection settings
     * 
     * @throws Exception  thrown if there is an error starting the server
     */
    public void start(Connection connection) throws Exception {

        SslContext sslCtx;

        if (connection.isSSL()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.DEBUG))
            .childHandler(new ChannelInitializer<SocketChannel> () {        
                @Override
                public void initChannel(SocketChannel ch)  {
                    if (sslCtx != null) {
                        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    ch.pipeline().addLast(new ObjectDecoder(10 * 1024 * 1024, ClassResolvers.cacheDisabled(null)));
                    ch.pipeline().addLast(encoder);
                    ch.pipeline().addLast("idleStateHandler", 
                        new IdleStateHandler(
                            connection.getIdleTimeout(), 
                            connection.getIdlePingTime(), 
                            0, 
                            TimeUnit.MILLISECONDS)
                    );
                    ch.pipeline().addLast(handlersExecutor, "heartbeatHandler", new ServerHeartbeatHandler(Server.this));
                    ch.pipeline().addLast("pingMessageHandler", pingMessageHandler);
                    ch.pipeline().addLast("pongMessageHandler", pongMessageHandler);

                    ch.pipeline().addLast("connectionHandler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            clients.add(ctx.channel());
                            ctx.pipeline().remove(this);
                            super.channelActive(ctx);
                        }
                    });
                    ch.pipeline().addLast(handlersExecutor, "serverMessageHandler", serverRequestHandler);
                    ch.pipeline().addLast("exceptionHandler", exceptionHandler);
                }
            });
        bootstrap.bind(connection.getPort()).sync();
            
    }

    /**
     * Shuts down the server.
     */
    public void shutdown() {
        handlersExecutor.shutdownGracefully();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        serverWasShutdown();
    }
    
    /**
     * Adds a client to the client/channel mapping.  This mapping is used to lookup
     * a channel when only the clientId is known.
     * 
     * Overriding classes should always call {@code super.addClient}
     * 
     * @param clientId      unique identifier for the client
     * @param channelId     {@link ChannelId} of the client
     * @throws CommunicationException thrown if the {@link ChannelId} does not exist
     */
    @Override
    public void addClient(UUID clientId, ChannelId channelId) throws CommunicationException {
        Channel channel = clients.find(channelId);
        if (channel == null)
            throw new CommunicationException("Invalid channelId during addClient.");
        this.clientIdToChannel.put(clientId, channel);
    }

    /**
     * Returns the {@link Channel} with the given {@link ChannelId}
     * 
     * @param channelId id of the channel to find
     * @return the matching {@link Channel} or {@code null} if no match was found
     */
    public Channel findChannel(ChannelId channelId) {
        return clients.find(channelId);
    }
    
    /**
     * Returns the ChannelId for a given clientId
     * 
     * @param clientId  the clientId used to lookup the {@link ChannelId}
     * @return the matching {@link ChannelId} or {@code null} if no match was found
     */
    public ChannelId findChannelId(UUID clientId) {
        return clientIdToChannel.get(clientId).id();
    }
    
    /**
     * Disconnect the specified client from the server.  
     * 
     * Overriding classes should always call this super-class implementation.
     * 
     * @param channelId the {@link ChannelId} identifying the client
     * @param reason    the reason that the disconnect is occurring
     * @throws CommunicationException thrown if there is an error while disconnecting the client
     */
    @Override
    public void disconnectClient(ChannelId channelId, DisconnectReason reason) throws CommunicationException {
        Channel channel = clients.find(channelId);
        if (channel == null)
            throw new CommunicationException("Invalid sessionId during disconnect");
        channel.disconnect().addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (!future.isSuccess()) {
                logger.error("Communication error", future.cause());
                throw new CommunicationException("Communication error during disconnect", future.cause());
            }
        });
    }
    
    /**
     * Send a callback to the client with the specified channelId
     * 
     * @param channelId     id of the {@link Client} channel
     * @param callback      {@link ClientCallback} object to send to client
     * @throws CommunicationException thrown if client channel cannot be found or is not connected
     */
    public void callback(ChannelId channelId, ClientCallback callback) throws CommunicationException {
        Channel ch = clients.find(channelId);
        if (ch == null) {
            throw new CommunicationException("Invalid sessionId during callback");
        }
        if (!ch.isActive()) {
            logger.error("Connection is not active");
            throw new CommunicationException("No connection to client during callback");
        }

        ch.writeAndFlush(callback).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (!future.isSuccess()) {
                logger.error("Communication error", future.cause());
                throw new CommunicationException("Communication error during callback", future.cause());
            }
        });
    }

    @Override
    public void handleException(String message, Exception ex) {
        logger.error(message, ex);  // default implementation; override for a more application specific implementation
    }
    
}
