package org.betawares.jorre.test.ping;

import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class PingServerTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "pingServerTest failed";

    @Test
    public void pingServerTest() {
        try {
            client.pingServer();
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
    }
    
}
