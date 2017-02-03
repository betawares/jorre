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

import io.netty.channel.ChannelId;
import java.util.UUID;

/**
 *  
 */
public interface ServerInterface {
    
    Version version();
    
    public void addClient(UUID clientId, ChannelId channelId) throws CommunicationException;
    
    public void disconnectClient(ChannelId channelId, DisconnectReason reason) throws CommunicationException;
    
    /**
     * Inform server that a client has been removed.
     * 
     * This is a good place to perform any addition cleanup
     * 
     * @param channelId     id of the client that was removed
     */
    public void clientRemoved(ChannelId channelId);
    
    /**
     * Inform server that it has been shutdown.  
     * 
     * This is a good place to perform any addition cleanup, i.e. close database connections, etc.
     */
    public void serverShutdown();
    
}
