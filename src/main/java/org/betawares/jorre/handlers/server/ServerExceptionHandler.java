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
import io.netty.channel.ChannelHandlerAdapter;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Server;

/**
 * Handles server-side exceptions that are not explicitly handled.
 * 
 */
@Sharable
public class ServerExceptionHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ServerExceptionHandler.class);
    
    private final Server server;

    public ServerExceptionHandler(Server server) {
        this.server = server;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Communications error with client - attempting to disconnect", cause);
        try {
            server.disconnectClient(ctx.channel().id(), DisconnectReason.IOError);
        } catch (CommunicationException ex) {
            logger.debug("Communications error - disconnect within exception failed", ex);
        }
    }
    
}
