package org.betawares.jorre.test;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.betawares.jorre.Server;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.Connection;
import org.betawares.jorre.ServerInterface;
import org.betawares.jorre.handlers.server.ServerHeartbeatHandler;

/**
 *
 * @author BetaSteward
 */
public class TestServer extends Server implements ServerInterface {

    private static TestServer instance;
    
    private long pingTime;
    private long pingCount = 0;
    
    private boolean testBoolean = false;
    
    public TestServer() {
        super(new Version(1, 0, 0, "test", ""));
    }

    public static void main(String[] args) throws Exception {
        getInstance().start(new Connection("", 1711));
    }

    public static TestServer getInstance() {
        if (instance == null)
            instance = new TestServer();
        return instance;
    }
    
    public long getPingTime() {
        return pingTime;
    }

    public void clearPingTime() {
        pingTime = 0;
    }

    public long getPingCount() {
        return pingCount;
    }
    
    public void clearPingCount() {
        pingCount = 0;
    }

    @Override
    public void clientWasRemoved(ChannelId channelId) {
        logger.info("TestServer: Removed client:" + channelId.asShortText());
    }

    @Override
    public void serverWasShutdown() {
        logger.info("TestServer: Server was shutdown");
    }
    
    public void pingClient(ChannelId channelId) throws CommunicationException {
        Channel ch = findChannel(channelId);
        if (ch == null)
            throw new CommunicationException("Invalid sessionId during pingClient");
        ServerHeartbeatHandler handler = (ServerHeartbeatHandler) ch.pipeline().get("heartbeatHandler");
        if (handler == null) 
            throw new CommunicationException("Unable to find heartbeatHandler");
        handler.pingClient();
    }

    public boolean getTestBoolean() {
        return testBoolean;
    }
    
    public void setTestBoolean(boolean b) {
        testBoolean = b;
    }

}
