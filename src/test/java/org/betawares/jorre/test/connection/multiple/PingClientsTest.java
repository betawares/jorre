package org.betawares.jorre.test.connection.multiple;

import io.netty.channel.ChannelId;
import org.betawares.jorre.CommunicationException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class PingClientsTest extends MultipleClientServerTest {

    private static final String FAIL_MESSAGE = "pingClientsTest failed";

    @Test
    @Ignore
    public void pingClientsTest() {
        server.clearPingTime();
        ChannelId channelId = server.findChannelId(client.id());
        pingClient(channelId);
        channelId = server.findChannelId(client2.id());
        pingClient(channelId);
        
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client2.isConnected());
//        Assert.assertTrue(FAIL_MESSAGE, server.getPingTime() > 0);
        Assert.assertEquals(FAIL_MESSAGE, 2, server.getPingCount());
    }

    private void pingClient(ChannelId channelId) {
        try {
            server.pingClient(channelId);
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
    }
    
}
