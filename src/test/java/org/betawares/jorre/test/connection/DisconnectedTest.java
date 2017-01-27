package org.betawares.jorre.test.connection;

import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.betawares.jorre.test.sendreceive.BooleanRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class DisconnectedTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "disconnectedTest failed";

    @Test
    public void disconnectedTest() {
        client.disconnect(DisconnectReason.Undefined, false);
        try {
            client.sendMessage(new BooleanRequest(true));
            Assert.fail();
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertFalse(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, server.getTestBoolean());
    }
    
}
