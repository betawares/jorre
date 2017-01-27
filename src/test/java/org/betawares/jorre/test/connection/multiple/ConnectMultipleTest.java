package org.betawares.jorre.test.connection.multiple;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class ConnectMultipleTest extends MultipleClientServerTest {

    private static final String FAIL_MESSAGE = "connectMultipleTest failed";

    @Test
    public void connectMultipleTest() {
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
        Assert.assertTrue(FAIL_MESSAGE, client2.isConnected());
    }
    
}
