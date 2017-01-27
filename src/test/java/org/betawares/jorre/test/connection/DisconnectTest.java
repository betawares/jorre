package org.betawares.jorre.test.connection;

import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.test.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class DisconnectTest extends ClientServerTest {

    private static final String FAIL_MESSAGE = "disconnectTest failed";

    @Test
    public void disconnectTest() {
        client.disconnect(DisconnectReason.Undefined, false);
        pauseForProcessing(FAIL_MESSAGE);
        Assert.assertFalse(FAIL_MESSAGE, client.isConnected());
    }
    
}
