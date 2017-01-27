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

package org.betawares.jorre.messages.responses;

import org.betawares.jorre.Client;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;

/**
 * Response object that returns a {@link Version} value from a {@link ConnectClientRequest}
 * 
 * @param <C> type of {@link Client} that will be passed to the handler
 */
public class ConnectClientResponse<C extends Client> extends ClientResponse<C> {
    
    private final Version version;
    
    public ConnectClientResponse(Version version) {
        this.version = version;
    }
    
    public Version getVersion() {
        return version;
    }

    /**
     * Checks the {@link Client} version against the {@link Server} version. 
     * 
     * If they don't match then the {@link Client} is disconnected 
     * 
     * @param client {@link Client} object that will be notified if successful or disconnected if the versions don't match
     */
    @Override
    public void handle(C client) {
        if (version.compareTo(client.version()) == 0) {
            client.connected();
        }
        else {
            client.disconnect(DisconnectReason.VersionMismatch, false);
            logger.error("Version mismatch between Client and Server.  Server version:" + version + "  Client version:" + client.version());
        }
    }
}
