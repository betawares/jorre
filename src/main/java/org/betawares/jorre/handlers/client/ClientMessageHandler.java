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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
import org.betawares.jorre.messages.requests.Request;
import org.betawares.jorre.messages.responses.ResponseFuture;


/**
 * Handles sending all {@link ServerMessage} messages and receiving all {@link ClientResponse} messages.
 * 
 * Calls the {@code handle} method of incoming {@link ClientCallback} and {@link ClientResponse} messages
 * 
 * @param <C> the type of {@link Client} that will handle messages
 */
public final class ClientMessageHandler<C extends ClientInterface> extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = Logger.getLogger(ClientMessageHandler.class);
    
    private ChannelHandlerContext ctx;
    private final Client client;
    // keep a list of all pending responses
    private final ConcurrentMap<UUID, ResponseFuture> responseMap = new ConcurrentHashMap<>();
    
    public ClientMessageHandler(Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
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
            callback.handle(client);
        }
        else if (msg instanceof ClientResponse) {
            ClientResponse response = (ClientResponse)msg;
            response.handle(client);
            // lookup the ResponseFuture for this response
            ResponseFuture future = responseMap.remove(response.id());
            if (future != null) {
                future.complete(response);
            }
            else {
                logger.error("Recieved a response with no matching future");
            }
        }
        else {
            logger.error("Invalid message recieved");
        }
    }
    
    /**
     * Send a {@link Message} to the {@link Server}
     * 
     * @param message   the {@link Message} to send
     * @throws CommunicationException   thrown if there is an error while sending the message
     */
    public void sendMessage(Message message) throws CommunicationException {
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
     * Send a {@link Request} to the {@link Server} and return a {@link ResponseFuture}
     * 
     * @param request   the {@link Request} to send to the {@link Server}
     * @return  a {@link ResponseFuture} that will be notified when the response has been received
     * @throws CommunicationException   thrown if there is an error while sending the request
     */
    public ResponseFuture sendRequest(Request request) throws CommunicationException {
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
        
}
