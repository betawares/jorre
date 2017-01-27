package org.betawares.jorre.test.connection;

import org.betawares.jorre.Connection;
import org.betawares.jorre.test.ClientServerTest;
import org.betawares.jorre.test.TestClient;
import org.betawares.jorre.test.TestClientBadVersion;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class BadVersionTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "badVersionTest failed";

    @Override
    protected TestClient createClient() {
        logger.info("Starting client");
        TestClient c = new TestClientBadVersion();
        c.connect(new Connection("localhost", 1711));
        pauseForProcessing("client setup error");
        return c;
    }

    @Test
    public void badVersionTest() {
        Assert.assertFalse(FAIL_MESSAGE, client.isConnected());
    }
}
