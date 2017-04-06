package org.betawares.jorre.test.sendreceive;

import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.test.TestClient;
import org.betawares.jorre.test.TestServer;

/**
 * Sends a boolean value to the server, saves it and responds with the same value
 */
public class BooleanRequestWithResponse extends ServerRequest<TestServer, TestClient> {
    
    private boolean b;
    
    public BooleanRequestWithResponse(boolean b) {
        this.b = b;
    }
    
    @Override
    public void handle(TestServer server, ChannelHandlerContext ctx) {
        server.setTestBoolean(b);
        try {
            this.respond(new BooleanResponse<TestClient>(b) {
                @Override
                public void handle(TestClient client) {
                    client.setBoolean(b);
                }
            }, ctx);
        } catch (CommunicationException ex) {
            logger.error("Error handling request", ex);
        }
    }
    
}