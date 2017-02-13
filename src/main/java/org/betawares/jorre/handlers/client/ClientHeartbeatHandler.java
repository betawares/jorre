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
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import org.betawares.jorre.Client;
import org.betawares.jorre.ClientInterface;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.PingMessage;

/**
 * Handles timeout events raised by the {@link IdleStateHandler} which is added to the {@link Client} pipeline.  
 * 
 * When WRITER_IDLE is raised a ping message is sent to the server.
 * 
 * When READER_IDLE is raised the client is disconnected.
 * 
 */
@Sharable
public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ClientHeartbeatHandler.class);
    
    private static final PingMessage PING = new PingMessage();

    private ChannelHandlerContext ctx;
    private final ClientInterface client;
    
    public ClientHeartbeatHandler(ClientInterface client) {
        this.client = client;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {  // no messages have been received, assume that server is down/unreachable
                client.disconnect(DisconnectReason.IdleTimeout, true);
                logger.info("Server has not communicated for timeout duration; disconnecting from server");
            } else if (e.state() == IdleState.WRITER_IDLE) {  // send ping to server to let it know that client is still alive
                pingServer();
                logger.debug("Sending ping");
            }
        }
    }
    
    /**
     * Send a ping message to the server.
     * 
     * @throws CommunicationException thrown if there is an error sending the ping message
     */
    public void pingServer() throws CommunicationException {
        ctx.writeAndFlush(PING).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (!future.isSuccess()) {
                logger.error("Communication error", future.cause());
                throw new CommunicationException("Error pinging server", future.cause());
            }
        });
    }
    
}
