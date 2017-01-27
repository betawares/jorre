package org.betawares.jorre.test.connection;

import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class ConnectTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "connectTest failed";

    @Test
    public void connectTest() {
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
    }
    
}
