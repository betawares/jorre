package org.betawares.jorre.test.ping;

import io.netty.channel.ChannelId;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class PingClientTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "pingClientTest failed";

    @Test
    @Ignore
    public void pingClientTest() {
        server.clearPingTime();
        server.clearPingCount();
        ChannelId channelId = server.findChannelId(client.id());
        try {
            server.pingClient(channelId);
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
//        Assert.assertTrue(FAIL_MESSAGE, server.getPingTime() > 0);
        Assert.assertEquals(FAIL_MESSAGE, 1, server.getPingCount());
    }

    @Test
    @Ignore
    public void pingManyClientTest() {
        server.clearPingTime();
        server.clearPingCount();
        ChannelId channelId = server.findChannelId(client.id());
        try {
            server.pingClient(channelId);
            server.pingClient(channelId);
            server.pingClient(channelId);
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
//        Assert.assertTrue(FAIL_MESSAGE, server.getPingTime() > 0);
        Assert.assertTrue(FAIL_MESSAGE, server.getPingCount() == 3);
    }
    
}
