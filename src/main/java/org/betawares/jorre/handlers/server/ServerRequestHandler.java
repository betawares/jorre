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

package org.betawares.jorre.handlers.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.Server;
import org.betawares.jorre.ServerInterface;
import org.betawares.jorre.messages.requests.ServerMessage;
import org.betawares.jorre.messages.requests.Request;

/**
 * Handles incoming {@link ServerMessage} messages.  This class will be shared amongst all client channels.
 * 
 * Executes the {@code handle} method of the {@link Request}
 * 
 * Informs server when a client needs to be removed
 * 
 * @param <S> the type of Server that will handle requests
 */
@Sharable
public class ServerRequestHandler<S extends ServerInterface> extends SimpleChannelInboundHandler<ServerMessage<S>> {
            
    private static final Logger logger = Logger.getLogger(ServerRequestHandler.class);

    private final S server;
    
    public ServerRequestHandler(S server) {
        this.server = server;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ServerMessage<S> msg) {
        try {
            msg.setReceivedTS();
            msg.handle(server, ctx);
        } catch (CommunicationException ex) {
            logger.error("Error handling message:", ex);
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.clientRemoved(ctx.channel().id());
    }
    
}
