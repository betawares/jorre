package org.betawares.jorre.test.sendreceive;

import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.test.TestClient;
import org.betawares.jorre.test.TestServer;

/**
 * Sends a boolean value to the server, saves it and but does not respond
 */
public class BooleanRequestWithNoResponse extends ServerRequest<TestServer, TestClient> {
    
    private boolean b;
    
    public BooleanRequestWithNoResponse(boolean b) {
        this.b = b;
    }
    
    @Override
    public void handle(TestServer server, ChannelHandlerContext ctx) {
        server.setTestBoolean(b);
        // by not responding, this will leave an orphan ResponseFuture in the ClientMessageHandler
    }
    
}