package org.betawares.jorre.test.connection.multiple;

import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.test.sendreceive.BooleanRequest;
import org.betawares.jorre.test.sendreceive.BooleanRequestWithResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class SendBooleanGetBooleanMultipleTest extends MultipleClientServerTest {
    
    private static final String FAIL_MESSAGE = "sendBooleanGetBooleanTest failed";

    @Test
    public void sendBooleanGetBooleanTest() {
        try {
            client.sendRequest(new BooleanRequestWithResponse(true));
            client2.sendRequest(new BooleanRequestWithResponse(false));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client2.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client.getBooleanTest());
        Assert.assertFalse(FAIL_MESSAGE, client2.getBooleanTest());
    }
    
}