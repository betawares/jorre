package org.betawares.jorre.test.connection;

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
public class ServerDisconnectTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "serverDisconnectTest failed";

    @Test
    public void serverDisconnectTest() {
        ChannelId channelId = server.findChannelId(client.id());
        try {
            TestServer.getInstance().disconnectClient(channelId, DisconnectReason.ForcedDisconnect);
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertFalse(FAIL_MESSAGE, client.isConnected());
    }
    
}
