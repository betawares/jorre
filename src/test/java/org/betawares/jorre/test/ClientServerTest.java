package org.betawares.jorre.test;

import org.apache.log4j.Logger;
import org.betawares.jorre.Connection;
import org.betawares.jorre.DisconnectReason;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class ClientServerTest {
    
    private static final int INTERVAL_UNIT = 50;
    
    protected static final Logger logger = Logger.getLogger(ClientServerTest.class);
    
    protected TestClient client;
    protected static TestServer server;
    
    public ClientServerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        if (server != null) {
            return;
        }
        new Thread(() -> {
            try {
                logger.info("Starting server");
                server = TestServer.getInstance();
                TestServer.main(new String[] {""});
            } catch (Exception ex) {
                logger.error("TestServer error", ex);
            }
        }).start();
        try {
            Thread.sleep(1000);  //wait for server to startup
        } catch (InterruptedException ex) {
            logger.error("setUpClass error", ex);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        client = createClient();
    }
    
    @After
    public void tearDown() {
        client.disconnect(DisconnectReason.UserDisconnect, false);
    }
    
    protected TestClient createClient() {
        logger.info("Starting client");
        TestClient c = new TestClient();
        c.connect(new Connection("localhost", 1711));
        pauseForProcessing("client setup error");
        return c;
    }
    
    protected void pauseForProcessing(String errorMessage) {
        try {
            Thread.sleep(INTERVAL_UNIT); // wait for processing to finish
        } catch (InterruptedException ex) {
            logger.error(errorMessage, ex);
            Assert.fail(errorMessage);
        }
    }
    
    protected void pauseForSeconds(String errorMessage, int seconds) {
        try {
            Thread.sleep(seconds * 1000); 
        } catch (InterruptedException ex) {
            logger.error(errorMessage, ex);
            Assert.fail(errorMessage);
        }
    }

    protected void pauseForever() {
        try {
            while (true) {
                Thread.sleep(Integer.MAX_VALUE); // wait forever - sometimes needed for debugging
            }
        } catch (InterruptedException ex) {
            logger.error("error", ex);
            Assert.fail("error");
        }
    }

}
