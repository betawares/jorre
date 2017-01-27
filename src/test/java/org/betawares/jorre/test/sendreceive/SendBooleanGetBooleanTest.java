package org.betawares.jorre.test.sendreceive;

import java.util.concurrent.ExecutionException;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

public class SendBooleanGetBooleanTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "sendBooleanGetBooleanTest failed";

    @Test
    public void sendBooleanGetBooleanTest() {
        BooleanResponse r = null;
        try {
            r = (BooleanResponse) client.sendBlockingRequest(new BooleanRequestWithResponse(true));
        } catch (CommunicationException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, r.response());
        Assert.assertTrue(FAIL_MESSAGE, client.getBooleanTest());
    }
    
}