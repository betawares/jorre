package org.betawares.jorre.test.callback;

import io.netty.channel.ChannelId;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.betawares.jorre.test.TestServer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class CallbackFailedTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "callbackFailedTest failed";
    
    @Test
    public void callbackFailedTest() {
        ChannelId channelId = server.findChannelId(client.id());
        try {
            client.disconnect(DisconnectReason.Undefined, false);
            server.callback(channelId, new BooleanCallback(false));
            Assert.fail();
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, !client.isConnected());
        Assert.assertNull(FAIL_MESSAGE, client.getBooleanTest());
    }
    
}
