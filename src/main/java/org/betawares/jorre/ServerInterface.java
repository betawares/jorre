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
 * Defines server-facing methods that will be available to {@link ServerMessage} 
 * and {@link ServerRequest} handlers.
 * 
 * Override this {@code interface} adding methods specific to your application.
 * 
 */
public interface ServerInterface {
    
    /**
     * Returns the version of the Server implementation.
     * A {@link Server} will not connect to a {@link Client} unless they
     * both have the same version.
     * 
     * @return the {@link Version} of the {@link Server} implementation
     */
    Version version();
    
    /**
     * Called when attempting to add a new client.
     * 
     * Overriding classes should always call {@code super.addClient}
     * 
     * @param clientId      unique identifier for the client
     * @param channelId     {@link ChannelId} of the client
     * @throws CommunicationException thrown if the {@link ChannelId} does not exist
     */
    public void addClient(UUID clientId, ChannelId channelId) throws CommunicationException;
    
    /**
     * Called when attempting to disconnect the specified client from the server.
     * 
     * Overriding classes should always call the super-class implementation.
     * 
     * @param channelId the {@link ChannelId} identifying the client
     * @param reason    the reason that the disconnect is occurring
     * @throws CommunicationException 
     */
    public void disconnectClient(ChannelId channelId, DisconnectReason reason) throws CommunicationException;
    
    /**
     * Inform server that a client has been removed.
     * 
     * This is a good place to perform any addition cleanup
     * 
     * @param channelId     id of the client that was removed
     */
    public void clientWasRemoved(ChannelId channelId);
    
    /**
     * Inform server that it has been shutdown.  
     * 
     * This is a good place to perform any addition cleanup, i.e. close database connections, etc.
     */
    public void serverWasShutdown();
    
    /**
     * Handle {@link Exception}s that are generated within a message handler
     * 
     * @param message description of exception
     * @param ex the exception to be handled
     */
    public void handleException(String message, Exception ex);
    
}
