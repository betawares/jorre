package org.betawares.jorre.test;

import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Version;

public class TestClientBadVersion extends TestClient {

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

    @Override
    public void setBoolean(boolean b) {
        test = b;
    }
    
    @Override
    public Boolean getBooleanTest() {
        return test;
    }


}
