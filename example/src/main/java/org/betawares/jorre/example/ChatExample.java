package org.betawares.jorre.example;

import java.util.UUID;
import org.apache.log4j.Logger;
import org.betawares.jorre.Connection;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;

public class ChatExample {

    private static final Logger logger = Logger.getLogger(ChatExample.class);

    public static void main(String[] args) {
        Version version = new Version(1, 0, 0, "", "");
        ChatClient client1 = new ChatClient(version, "chat client 1");
        ChatClient client2 = new ChatClient(version, "chat client 2");
        
        // run server in a background thread
        Thread serverThread = new Thread(() -> {
            try {
                logger.info("Starting chat server");
                ChatServer.instance().start(44556, false);
                logger.info("Finished starting chat server");
            } catch (Exception ex) {
                logger.error("Error starting chat server", ex);
            }
        });
        serverThread.start();

        pause();  //wait for server to startup

        Connection connection = new Connection("localhost", 44556);
        client1.connect(connection);
        client2.connect(connection);
        
        pause();  //wait for clients to connect
        
        try {
            if (!client1.signIn())
                logger.error(client1.userName() + " could not sign in");
            
            UUID roomId = client1.createChatRoom(client1.userName() + " room");
            client1.joinChat(roomId);
            client1.sendChatMessage(roomId, "message 1");
            
            if (!client2.signIn())
                logger.error(client1.userName() + "could not sign in");
            client2.joinChat(roomId);

            client2.sendChatMessage(roomId, "message 2");
                        
        } catch (CommunicationException ex) {
            logger.error("ChatExample error", ex);
        }
        
        pause();  //wait for clients to finish sending/receiving
        
        ChatServer.instance().shutdown();

    }
    
    private static void pause() {
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException ex) {
            logger.error("ChatExample error", ex);
        }
    }
    
}
