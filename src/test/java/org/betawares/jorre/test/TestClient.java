package org.betawares.jorre.test;

import org.betawares.jorre.Client;
import org.betawares.jorre.ClientInterface;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.handlers.client.ClientHeartbeatHandler;

public class TestClient extends Client implements ClientInterface {

    private Boolean test;
    
    public TestClient() {
        super(new Version(1, 0 , 0, "test", ""));
    }
    
    public TestClient(Version version) {
        super(version);
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

    public void pingServer() throws CommunicationException {
        ClientHeartbeatHandler heartbeatHandler = (ClientHeartbeatHandler)channel().pipeline().get("heartbeatHandler");
        heartbeatHandler.pingServer();
    }    

}
