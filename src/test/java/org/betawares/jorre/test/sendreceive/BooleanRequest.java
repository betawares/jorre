package org.betawares.jorre.test.sendreceive;

import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.messages.requests.ServerMessage;
import org.betawares.jorre.test.TestServer;

/**
 * Sends a boolean value to the server and saves it
 */
public class BooleanRequest extends ServerMessage<TestServer> {
    
    private boolean b;
    
    public BooleanRequest(boolean b) {
        this.b = b;
    }
    
    @Override
    public void handle(TestServer server, ChannelHandlerContext ctx) {
        server.setTestBoolean(b);
    }
    
}