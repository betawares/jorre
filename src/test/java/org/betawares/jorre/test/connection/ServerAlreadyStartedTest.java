package org.betawares.jorre.test.connection;

import java.net.BindException;
import org.betawares.jorre.Connection;
import org.betawares.jorre.test.ClientServerTest;
import org.betawares.jorre.test.TestClient;
import org.betawares.jorre.test.TestClientBadVersion;
import org.betawares.jorre.test.TestServer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author BetaSteward
 */
public class ServerAlreadyStartedTest extends ClientServerTest {
    
    private static final String FAIL_MESSAGE = "serverAlreadyStartedTest failed";

    @Test
    public void serverAlreadyStartedTest() {
        new Thread(() -> {
            try {
                logger.info("Starting server again");
                TestServer.main(new String[] {""});
                Assert.fail(FAIL_MESSAGE);  //if we get here then server started twice
            } catch (BindException ex) {
                logger.error("Server alreay started", ex);
            } catch (Exception ex) {
                logger.error("TestServer error", ex);
                Assert.fail(FAIL_MESSAGE);
            }
        }).start();
        pauseForProcessing(FAIL_MESSAGE);
    }
}
