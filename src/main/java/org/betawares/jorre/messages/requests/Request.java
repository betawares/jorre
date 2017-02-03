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

package org.betawares.jorre.messages.requests;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.Client;
import org.betawares.jorre.ClientInterface;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.Server;
import org.betawares.jorre.ServerInterface;
import org.betawares.jorre.messages.responses.ClientResponse;

/**
 * Base class for all requests to a {@link Server} expecting a response.
 * 
 * Overriding classes should provide a server type that extends {@link Server}.
 * Overriding classes should provide a client type that extends {@link Client}.
 * Overriding classes must implement the {@code handle} method.  
 * 
 * The convenience method {@code respond} can be used to return results to the {@link Client}
 * 
 * @param <S> the type of Server that will be generating the response
 * @param <C> the type of Client that will be processing the response
 */
public abstract class Request<S extends ServerInterface, C extends ClientInterface> extends ServerMessage<S> {
        
    /**
     * Send a response to the {@link Client}.
     * 
     * @param response  the response object to return of type {@link T}
     * @param ctx       the {@link ChannelHandlerContext} for the {@link Channel} to the {@link Client}
     * 
     * @throws CommunicationException thrown if an error occurs sending the response
     */
    protected void respond(ClientResponse<C> response, ChannelHandlerContext ctx) throws CommunicationException {
        // set the reponse id to be the same as the request to that they can be matched
        response.id(id);
        if (!ctx.channel().isActive()) {
            logger.error("Connection is not active");
            throw new CommunicationException("No connection to client");
        }
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (future.isSuccess())
                response.setSentTS();
            else
                logger.error("Communication error", future.cause());
        });
    }

}
