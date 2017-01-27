package org.betawares.jorre.test.sendreceive;

import java.util.concurrent.ExecutionException;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.messages.responses.ResponseFuture;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

public class SendBooleanGetFutureTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "sendBooleanGetFutureTest failed";

    @Test
    public void sendBooleanGetFutureTest() {
        BooleanResponse r = null;
        ResponseFuture f = null;
        try {
            f = client.sendRequest(new BooleanRequestWithResponse(true));
            r = (BooleanResponse) f.get();
        } catch (CommunicationException | InterruptedException | ExecutionException ex) {
            logger.error(FAIL_MESSAGE, ex);
            Assert.fail();
        }
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertNotNull(FAIL_MESSAGE, f);
        Assert.assertNotNull(FAIL_MESSAGE, r);
        Assert.assertTrue(FAIL_MESSAGE, r.response());
        Assert.assertTrue(FAIL_MESSAGE, server.getTestBoolean());
    }
    
}