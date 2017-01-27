package org.betawares.jorre.test.connection;

import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class IdleTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "idleTest failed";

    @Test
    @Ignore
    public void idleTest() {
        pauseForSeconds(FAIL_MESSAGE, 80);
        Assert.assertTrue(FAIL_MESSAGE, client.isConnected());
    }
    
}
