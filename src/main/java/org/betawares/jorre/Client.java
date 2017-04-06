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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;

import org.apache.log4j.Logger;
import org.betawares.jorre.handlers.PingMessageHandler;
import org.betawares.jorre.handlers.PongMessageHandler;
import org.betawares.jorre.handlers.client.ClientMessageHandler;
import org.betawares.jorre.handlers.client.ClientHeartbeatHandler;
import org.betawares.jorre.handlers.client.ClientMessageInspector;
import org.betawares.jorre.messages.requests.ServerMessage;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.ClientResponse;
import org.betawares.jorre.messages.responses.ResponseFuture;


/**
 * Abstract base class for client implementations.
 * 
 * Overriding classes should add methods for application specific features.
 * 
 * 
 */
public abstract class Client implements ClientInterface {

    protected static final Logger logger = Logger.getLogger(Client.class);

    private ClientMessageHandler clientMessageHandler;
    
    private final UUID id;

    private SslContext sslCtx;
    private Channel channel;
    private EventLoopGroup group;
    
    private final Version version;

    /**
     * Creates a new <code>Client</code> with the specified {@link Version}
     * 
     * @param version the {@link Version} of the {@link Client} implementation
     * 
     */
    public Client(Version version) {
        this.version = version;
        
        id = UUID.randomUUID();
    }

    /**
     * Connect to a {@link Server} using the specified {@link Connection} settings.
     * 
     * @param connection    a {@link Connection} instance specifying the connection settings
     */
    public void connect(Connection connection) {

        clientMessageHandler = new ClientMessageHandler(this, connection.getMaxResponseAge());
        group = new NioEventLoopGroup();
        try {
            if (connection.isSSL()) {
                sslCtx = SslContextBuilder.forClient().build();
            } else {
                sslCtx = null;
            }
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel> () {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        if (sslCtx != null) {
                            ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), connection.getHost(), connection.getPort()));
                        }
                        ch.pipeline().addLast(new ObjectDecoder(10 * 1024 * 1024, ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast(new ObjectEncoder());
//                        ch.pipeline().addLast("messageInspector", new ClientMessageInspector());
                        ch.pipeline().addLast("idleStateHandler", 
                            new IdleStateHandler(
                                connection.getIdleTimeout(), 
                                connection.getIdlePingTime(), 
                                0, 
                                TimeUnit.MILLISECONDS)
                        );
                        ch.pipeline().addLast("heartbeatHandler", new ClientHeartbeatHandler(Client.this));
                        ch.pipeline().addLast("pingMessageHandler", new PingMessageHandler());
                        ch.pipeline().addLast("pongMessageHandler", new PongMessageHandler());

                        ch.pipeline().addLast("clientMessageHandler", clientMessageHandler);

                        ch.pipeline().addLast("exceptionHandler", new ChannelHandlerAdapter() {    
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                logger.error("Communications error", cause);
                                Client.this.disconnect(DisconnectReason.IOError, true);
                            }    
                        });
                    }
                });

            channel = bootstrap.connect(connection.getHost(), connection.getPort()).sync().channel();
        } catch (SSLException | InterruptedException ex) {
            logger.fatal("Error connecting", ex);
            disconnect(DisconnectReason.IOError, true);
        }
    }
    
    @Override
    public void disconnect(DisconnectReason reason, boolean error) {
        try {
            if (channel.isActive()) {
                channel.disconnect().sync();
            }
        } catch (InterruptedException ex) {
            logger.fatal("Error disconnecting", ex);
        }
        finally {
            disconnected(reason, error);
            group.shutdownGracefully();
        }
    }

    /**
     * Returns a boolean indicating whether the client is connected to a server 
     * and the client has been registered with that server
     * 
     * @return {@code true} if the client is connected
     */
    public boolean isConnected() {
        if (clientMessageHandler != null && clientMessageHandler.isConnected()) {
            if (channel != null) {
                return channel.isActive();
            }
        }
        return false;
    }
    
    /**
     * Send a {@link ServerMessage} to the server
     * 
     * @param message   the {@link ServerMessage} to send to the server
     * @throws CommunicationException thrown if there is an error while sending the message
     */
    public void sendMessage(ServerMessage message) throws CommunicationException {
        if (clientMessageHandler == null) {
            throw new CommunicationException("Not connected");
        }
        clientMessageHandler.sendMessage(message);
    }

    /**
     * Send a {@link ServerRequest} to the server and return a {@link ResponseFuture}
     * 
     * @param request   the {@link ServerRequest} to send to the server
     * @return  returns a {@link ResponseFuture} for the request
     * @throws CommunicationException thrown if there is an error while sending the request
     */
    public ResponseFuture sendRequest(ServerRequest request) throws CommunicationException {
        if (clientMessageHandler == null) {
            throw new CommunicationException("Not connected");
        }        
        return clientMessageHandler.sendRequest(request);
    }

    /**
     * Send a {@link ServerRequest} to the server and wait for a {@link ClientResponse}
     * 
     * @param request   the {@link ServerRequest} to send to the server
     * @return  returns the {@link ClientResponse} sent back from the server
     * @throws CommunicationException thrown if there is an error while sending the request or receiving the response
     */
    public ClientResponse sendBlockingRequest(ServerRequest request) throws CommunicationException {
        if (clientMessageHandler == null) {
            throw new CommunicationException("Not connected");
        }        
        try {
            return clientMessageHandler.sendRequest(request).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new CommunicationException("Error waiting for response", ex);
        }
    }
    
    @Override
    public UUID id() {
        return id;
    }
    
    /**
     * Returns the {@link Channel} associated with this {@link Client}.
     * 
     * @return Returns the {@link Channel} associated with this {@link Client}
     */
    public Channel channel() {
        return channel;
    }

    @Override
    public Version version() {
        return version;
    }

    @Override
    public void handleException(String message, Exception ex) {
        logger.error(message, ex);  // default implementation; override for a more specific implementation
    }

}
