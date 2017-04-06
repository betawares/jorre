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

package org.betawares.jorre.handlers.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.betawares.jorre.Client;
import org.betawares.jorre.ClientInterface;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.messages.Message;
import org.betawares.jorre.messages.callback.ClientCallback;
import org.betawares.jorre.messages.requests.ConnectClientRequest;
import org.betawares.jorre.messages.requests.ServerMessage;

import org.betawares.jorre.messages.responses.ClientResponse;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.ResponseFuture;


/**
 * Handles sending  {@link ServerMessage} and {@link ServerRequest} messages to 
 * the server.  Also handles receiving {@link ClientResponse} and {@link ClientCallback} messages.                                                       
 * 
 * Calls the {@code handle} method of incoming {@link ClientCallback} and {@link ClientResponse} messages
 * 
 * For each {@link ServerRequest} a corresponding {@link ResponseFuture} is stored 
 * until a matching {@link ClientResponse} is received.  If no response is received before 
 * the maximum age is reached then the {@link ResponseFuture} is removed and an exception
 * is generated.
 * 
 * @param <C> the type of {@link Client} that will handle messages
 */
public final class ClientMessageHandler<C extends ClientInterface> extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = Logger.getLogger(ClientMessageHandler.class);
    
    private ChannelHandlerContext ctx;
    private final C client;
    
    // keep a list of all pending responses so that they can be mapped to the approriate future
    private final ConcurrentMap<Long, ResponseFuture> responseMap = new ConcurrentHashMap<>();
    // thread pool for handling responses
    private final ThreadPoolExecutor responseExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    // scheduler to cleanup the responseMap
    private final ScheduledExecutorService responseMapCleaner = Executors.newScheduledThreadPool(1);
    
    private final long maxResponseAge;  // measured in milliseconds
    
    /**
     * 
     * @param client the {@link ClientInterface} that will be notified and passed to handlers
     * @param maxResponseAge the maximum time to wait in millisecond for a response
     */
    public ClientMessageHandler(C client, long maxResponseAge) {
        this.client = client;
        this.maxResponseAge = maxResponseAge;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        responseMapCleaner.scheduleAtFixedRate(() -> {
            responseMap.forEach((key, value) -> {
                if (value.age() > maxResponseAge) {
                    responseMap.remove(key).completeExceptionally(new CommunicationException("Response timed-out"));
                }
            });
        }, maxResponseAge, maxResponseAge, TimeUnit.MILLISECONDS);
        sendRequest(new ConnectClientRequest(client.id(), client.version()));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        client.disconnect(DisconnectReason.IOError, true);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        msg.setReceivedTS();
        if (msg instanceof ClientCallback) {
            ClientCallback callback = (ClientCallback)msg;
            responseExecutor.execute(() -> {
                callback.handle(client);
            });
        }
        else if (msg instanceof ClientResponse) {
            ClientResponse response = (ClientResponse)msg;
            responseExecutor.execute(() -> {
                response.handle(client);
                // lookup the ResponseFuture for this response
                ResponseFuture future = responseMap.remove(response.requestId());
                if (future != null) {
                    future.complete(response);
                }
                else {
                    logger.error("Recieved a response with no matching future");
                }
            });
        }
        else {
            logger.error("Invalid message recieved");
        }
    }
    
    /**
     * Send a {@link ServerMessage} to the {@link Server}
     * 
     * @param message   the {@link ServerMessage} to send
     * @throws CommunicationException   thrown if there is an error while sending the message
     */
    public void sendMessage(ServerMessage message) throws CommunicationException {
        if (!ctx.channel().isActive()) {
            logger.error("Connection is not active");
            throw new CommunicationException("No connection to server");
        }
        ctx.writeAndFlush(message).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (future.isSuccess()) {
                message.setSentTS();
            }
            else {
                logger.error("Communication error", future.cause());
                throw new CommunicationException("Communication error", future.cause());
            }
        });
    }
    
    /**
     * Send a {@link ServerRequest} to the {@link Server} and return a {@link ResponseFuture}
     * 
     * @param request   the {@link ServerRequest} to send to the {@link Server}
     * @return  a {@link ResponseFuture} that will be notified when the response has been received
     * @throws CommunicationException   thrown if there is an error while sending the request
     */
    public ResponseFuture sendRequest(ServerRequest request) throws CommunicationException {
        if (!ctx.channel().isActive()) {
            logger.error("Connection is not active");
            throw new CommunicationException("No connection to server");
        }

        ResponseFuture response = new ResponseFuture();
        ctx.writeAndFlush(request).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (future.isSuccess()) {
                request.setSentTS();
                // add the ResponseFuture to the map so that we can later map the respsone to it
                responseMap.put(request.id(), response);
            }
            else {
                logger.error("Communication error", future.cause());
                response.completeExceptionally(future.cause());
            }
        });
        return response;
    }

    /**
     * 
     * @return {@code true} if the client is fully connected and the channel is active
     */
    public boolean isConnected() {
        return (ctx != null);
    }
        
}
