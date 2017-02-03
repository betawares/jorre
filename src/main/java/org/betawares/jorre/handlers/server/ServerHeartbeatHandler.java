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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.messages.PingMessage;
import org.betawares.jorre.messages.PongMessage;
import org.betawares.jorre.Server;

/**
 * Handles timeout events raised by the {@link IdleStateHandler} which is added to the {@link Server} pipeline.  
 * 
 * When WRITER_IDLE is raised a ping message is sent to the client.
 * 
 * When READER_IDLE is raised the client is disconnected.
 */
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ServerHeartbeatHandler.class);
    
    private static final PingMessage PING = new PingMessage();
    
    private ChannelHandlerContext ctx;
    private long startTime;
    
    private final Server server;
    
    public ServerHeartbeatHandler (Server server) {
        this.server = server;
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
            if (e.state() == IdleState.READER_IDLE) {  // no messages have been received, assume that client is down/unreachable
                server.disconnectClient(ctx.channel().id(), DisconnectReason.IdleTimeout);
                logger.info(ctx.channel().id() + " disconnected due to extended idle");
            } else if (e.state() == IdleState.WRITER_IDLE) {  // send ping to client to let it know that server is still alive
                pingClient();
                logger.debug("Sending ping");
            }
        }
    }
    
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        if (msg instanceof PongMessage) {
//            long milliSeconds = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
//            server.pingTime(ctx.channel().id(), milliSeconds);
//        }
//        ctx.fireChannelRead(msg);
//    }

    /**
     * Send a ping message to the client.
     */
    public void pingClient() {
        startTime = System.nanoTime();
        ctx.writeAndFlush(PING).addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (!future.isSuccess()) {
                logger.error("Communication error", future.cause());
            }
        });
    }
    
}
