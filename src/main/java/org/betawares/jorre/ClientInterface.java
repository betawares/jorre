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

import java.util.UUID;

/**
 * Defines client-facing methods that will be available to {@link ClientResponse} 
 * and {@link ClientCallBack} handlers.
 * 
 * Override this {@code interface} adding methods specific to your application.
 * 
 */
public interface ClientInterface {

    /**
     * Returns the client version.
     * 
     * @return Returns the version of this {@link Client}.
     */
    Version version();
    
    /**
     * Inform the client that it has been disconnected.
     * 
     * @param reason reason that the client was disconnected
     * @param error was the disconnection due to an error
     */
    public void disconnected(DisconnectReason reason, boolean error);

    /**
     * Inform the client that it has been connected to a server.
     */
    public void connected();
    
    /**
     * Disconnect the client from the server.  Use this for deliberate user initiated disconnects
     */
    default public void disconnect() {
        disconnect(DisconnectReason.UserDisconnect, false);
    }
    
    /**
     * Disconnect the client from the server.
     * 
     * @param reason reason for disconnecting the client
     * @param error is this in response to an error
     */
    public void disconnect(DisconnectReason reason, boolean error);

    /**
     * Returns the client id.
     * 
     * @return Returns the globally unique identifier of this {@link Client}.
     */
    public UUID id();
    
    /**
     * Handle {@link Exception}s that are generated within a message handler
     * 
     * @param message description of exception
     * @param ex the exception to be handled
     */
    public void handleException(String message, Exception ex);

}
