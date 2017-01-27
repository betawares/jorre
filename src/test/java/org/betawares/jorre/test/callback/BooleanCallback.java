package org.betawares.jorre.test.callback;

import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.messages.callback.ClientCallback;
import org.betawares.jorre.test.TestClient;

/**
 *
 * @author BetaSteward
 */
public class BooleanCallback extends ClientCallback<TestClient> {

    private final boolean b;
    
    public BooleanCallback(boolean b) {
        this.b = b;
    }
    
    @Override
    public void handle(TestClient client) {
        client.setBoolean(b);
    }

    
}