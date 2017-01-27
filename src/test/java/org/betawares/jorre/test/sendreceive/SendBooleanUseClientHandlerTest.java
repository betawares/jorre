package org.betawares.jorre.test.sendreceive;

import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

public class SendBooleanUseClientHandlerTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "sendBooleanUseClientHandlerTest failed";

    @Test
    public void sendBooleanUseClientHandlerTest() {
        try {
            client.sendRequest(new BooleanRequestWithResponse(true));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, server.getTestBoolean());
        Assert.assertTrue(FAIL_MESSAGE, client.getBooleanTest());
    }
    
}