package org.betawares.jorre.test;

import org.apache.log4j.Logger;
import org.betawares.jorre.Client;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Version;

/**
 *
 * @author BetaSteward
 */
public class TestClientBadVersion extends TestClient {
    private static final Logger logger = Logger.getLogger(TestClientBadVersion.class);

    private Boolean test;
    
    public TestClientBadVersion() {
        super(new Version(1, 1 , 1, "bad test", ""));
    }
    
    @Override
    public void connected() {
        logger.info("TestClient: Connected");
    }

    @Override
    public void disconnected(DisconnectReason reason, boolean error) {
        logger.info("TestClient: Disconnect recieved - " + error);
    }

    public void setBoolean(boolean b) {
        test = b;
    }
    
    public Boolean getBooleanTest() {
        return test;
    }


}
