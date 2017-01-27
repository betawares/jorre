package org.betawares.jorre.test.connection.multiple;

import io.netty.channel.ChannelId;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.callback.BooleanCallback;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class CallbackClientsTest extends MultipleClientServerTest {

    private static final String FAIL_MESSAGE = "callbackClientsTest failed";

    @Test
    public void callbackClientsTest() {
        ChannelId channelId = server.findChannelId(client.id());
        sendBooleanClient(channelId, true);
        channelId = server.findChannelId(client2.id());
        sendBooleanClient(channelId, false);
        
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client2.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client.getBooleanTest());
        Assert.assertFalse(FAIL_MESSAGE, client2.getBooleanTest());
    }

    private void sendBooleanClient(ChannelId channelId, boolean b) {
        try {
            server.callback(channelId, new BooleanCallback(b));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
    }
    
}
