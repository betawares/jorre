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
public class CallbackReceivedTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "callbackReceivedTest failed";

    @Test
    public void callbackReceivedTest() {
        ChannelId channelId = server.findChannelId(client.id());
        try {
            server.callback(channelId, new BooleanCallback(false));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertFalse(FAIL_MESSAGE, client.getBooleanTest());
    }
    
}

