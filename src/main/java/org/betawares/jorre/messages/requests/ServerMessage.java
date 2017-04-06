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

import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import org.betawares.jorre.Server;
import org.betawares.jorre.ServerInterface;
import org.betawares.jorre.messages.Message;

/**
 * Base class for all messages to a {@link Server} that do not expect a response.
 * 
 * Overriding classes should provide a server type that extends {@link Server}.
 * Overriding classes must implement the {@code handle} method. 
 *  
 * @param <S> the type of Server that will be processing the {@link ServerMessage}
 */
public abstract class ServerMessage<S extends ServerInterface> extends Message {
        
    protected static final Logger logger = Logger.getLogger(ServerMessage.class);

    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    protected final long id = NEXT_ID.getAndIncrement();
    
    public long id() {
        return id;
    }
    
    /**
     * Handles server side processing for Messages.
     * 
     * This method will be called in response to an incoming {@link ServerMessage}.  Overriding
     * classes will provide an implementation that handles the message.
     * 
     * @param server    {@link Server} object that can be referenced in the handler
     * @param ctx       the {@link ChannelHandlerContext} for the {@link Channel} to the {@link Client}
     */
    public abstract void handle(S server, ChannelHandlerContext ctx);
        
}
