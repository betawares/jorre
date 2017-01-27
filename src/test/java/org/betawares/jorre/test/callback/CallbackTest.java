package org.betawares.jorre.test.callback;

import io.netty.channel.ChannelId;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class CallbackTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "callbackTest failed";

    @Test
    public void callbackTest() {
        ChannelId channelId = server.findChannelId(client.id());
        try {
            server.callback(channelId, new BooleanCallback(true));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client.getBooleanTest());
    }
    
}
