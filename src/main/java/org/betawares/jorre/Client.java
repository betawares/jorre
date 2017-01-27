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
import javax.net.ssl.SSLException;

import org.apache.log4j.Logger;
import org.betawares.jorre.handlers.PingMessageHandler;
import org.betawares.jorre.handlers.PongMessageHandler;
import org.betawares.jorre.handlers.client.ClientMessageHandler;
import org.betawares.jorre.handlers.client.ClientHeartbeatHandler;
import org.betawares.jorre.messages.Message;
import org.betawares.jorre.messages.requests.Request;
import org.betawares.jorre.messages.responses.ClientResponse;
import org.betawares.jorre.messages.responses.ResponseFuture;


/**
 * Abstract base class for client implementations.
 * 
 * A client can connect to a server which is listening on a specified port
 * 
 * Uses Netty (see <a href="http://netty.io">http://netty.io</a>).
 * 
 */
public abstract class Client {

    private static final Logger logger = Logger.getLogger(Client.class);

    private static final int DEFAULT_IDLE_PING_TIME = 30;
    private static final int DEFAULT_IDLE_TIMEOUT = 60;  

    private final ClientMessageHandler clientMessageHandler;
    
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
        clientMessageHandler = new ClientMessageHandler(this);
    }

    /**
     * Starts the Server.
     * 
     * Shortcut method for {@link #connect(Connection, int, int)} with default idle time values.
     * 
     * @param connection    a {@link Connection} instance specifying the connection parameters
     */
    public void connect(Connection connection) {
        connect(connection, DEFAULT_IDLE_PING_TIME, DEFAULT_IDLE_TIMEOUT);
    }

    /**
     * Connect to a {@link Server} using the specified {@link Connection} parameters
     * 
     * @param connection    a {@link Connection} instance specifying the connection parameters
     * @param idlePingTime    number of seconds without communication from the {@link Server} before sending a ping message; 
     *                        this value should be less than {@code idleTimeout}
     * @param idleTimeout     number of seconds without communication from the {@link Server} before disconnecting
     * 
     */
    public void connect(Connection connection, int idlePingTime, int idleTimeout) {

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
                        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(idleTimeout, idlePingTime, 0));
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

    /**
     * Inform the client that it has been disconnected.
     * 
     * @param reason reason that the client was disconnected
     * @param error was the disconnection due to an error
     */
    public abstract void disconnected(DisconnectReason reason, boolean error);

    /**
     * Inform the client that it has been connected to a server.
     */
    public abstract void connected();

    /**
     * Disconnect the client from the server.  Use this for deliberate user initiated disconnects
     */
    public void disconnect() {
        disconnect(DisconnectReason.UserDisconnect, false);
    }
    
    /**
     * Disconnect the client from the server.
     * 
     * @param reason reason for disconnecting the client
     * @param error is this in response to an error
     */
    public void disconnect(DisconnectReason reason, boolean error) {

        try {
            if (channel.isActive()) {
                channel.disconnect().sync();
                disconnected(reason, error);
            }
        } catch (InterruptedException ex) {
            logger.fatal("Error disconnecting", ex);
        }
        finally {
            group.shutdownGracefully();
        }
    }

    /**
     * Returns a boolean indicating whether the client is connected to a server or not
     * 
     * @return flag indicating connection status
     */
    public boolean isConnected() {
        if (channel != null) {
            return channel.isActive();
        }
        return false;
    }
    
    /**
     * Send a {@link Message} to the server
     * 
     * @param message   the {@link Message} to send to the server
     * @throws CommunicationException thrown if there is an error while sending the message
     */
    public void sendMessage(Message message) throws CommunicationException {
        clientMessageHandler.sendMessage(message);
    }

    /**
     * Send a {@link Request} to the server and return a {@link ResponseFuture}
     * 
     * @param request   the {@link Request} to send to the server
     * @return  returns a {@link ResponseFuture} for the request
     * @throws CommunicationException thrown if there is an error while sending the request
     */
    public ResponseFuture sendRequest(Request request) throws CommunicationException {
        return clientMessageHandler.sendRequest(request);
    }

    /**
     * Send a {@link Request} to the server and wait for a {@link ClientResponse}
     * 
     * @param request   the {@link Request} to send to the server
     * @return  returns the {@link ClientResponse} sent back from the server
     * @throws CommunicationException thrown if there is an error while sending the request or receiving the response
     */
    public ClientResponse sendBlockingRequest(Request request) throws CommunicationException {
        try {
            return clientMessageHandler.sendRequest(request).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new CommunicationException("Error waiting for response", ex);
        }
    }
    
    /**
     * Returns the client id.
     * 
     * @return Returns the globally unique identifier of this {@link Client}.
     */
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

    /**
     * Returns the client version.
     * 
     * @return Returns the version of this {@link Client}.
     */
    public Version version() {
        return version;
    }
}
