package org.betawares.jorre.test.connection.multiple;

import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.test.ClientServerTest;
import org.betawares.jorre.test.TestClient;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author BetaSteward
 */
public abstract class MultipleClientServerTest extends ClientServerTest {

    protected TestClient client2;

    @Before
    @Override
    public void setUp() {
        logger.info("Starting clients");
        client = createClient();
        client2 = createClient();
    }

    @After
    @Override
    public void tearDown() {
        client.disconnect(DisconnectReason.UserDisconnect, false);
        client2.disconnect(DisconnectReason.UserDisconnect, false);
    }
    
}
