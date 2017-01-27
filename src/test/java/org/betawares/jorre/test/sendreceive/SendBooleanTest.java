package org.betawares.jorre.test.sendreceive;

import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class SendBooleanTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "sendBooleanTest failed";

    @Test
    public void sendBooleanTest() {
        try {
            client.sendMessage(new BooleanRequest(true));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, server.getTestBoolean());
    }
    
}
