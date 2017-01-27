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
import java.util.UUID;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Server;
import org.betawares.jorre.Version;
import org.betawares.jorre.messages.responses.ConnectClientResponse;

/**
 *
 * Handles {@link Client} connection requests.
 * 
 * The request is rejected if there is a version mismatch between the {@link Client} and the {@link Server}.
 * 
 * If successful then the server is informed by calling {@code addClient} and a {@link ConnectClientResponse} is sent as a response.
 * 
 */
public class ConnectClientRequest extends Request {
    
    private final UUID clientId;
    private final Version clientVersion;

    public ConnectClientRequest(UUID clientId, Version clientVersion) {
        this.clientId = clientId;
        this.clientVersion = clientVersion;
    }
    
    @Override
    public void handle(Server server, ChannelHandlerContext ctx) throws CommunicationException {
        Version version = server.version();
        if (version.compareTo(clientVersion) == 0) {
            server.addClient(clientId, ctx.channel().id());
            respond(ctx, new ConnectClientResponse(version));
        }
        else {
            logger.error("Version mismatch");
            respond(ctx, new ConnectClientResponse(new Version()));  // send poison pill
            server.disconnectClient(ctx.channel().id(), DisconnectReason.ValidationError);
        }
    }
    
    public UUID clientId() {
        return clientId;
    }

    private void respond(ChannelHandlerContext ctx, ConnectClientResponse response) {
        response.id(id);
        if (!ctx.channel().isActive()) {
            logger.error("Connection is not active");
            return;
        }
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (!future.isSuccess()) {
                logger.error("Communication error", future.cause());
            }
        });
    }
    
}
