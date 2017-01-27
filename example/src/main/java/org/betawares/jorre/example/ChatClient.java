package org.betawares.jorre.example;

import java.util.UUID;
import org.apache.log4j.Logger;
import org.betawares.jorre.Client;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.Request;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.messages.responses.UUIDResponse;


public class ChatClient extends Client {

    private static final Logger logger = Logger.getLogger(ChatClient.class);
    
    private final String userName;
    
    public ChatClient(Version version, String userName) {
        super(version);
        this.userName = userName;
    }
    
    public boolean signIn() throws CommunicationException {
        Request req = new SignInRequest(userName);
        BooleanResponse r = (BooleanResponse)sendBlockingRequest(req);
        return r.response();
    }
    
    public void signedIn() {
        logger.info(userName + " signedIn");
    }
    
    public UUID createChatRoom(String info) throws CommunicationException {
        Request req = new CreateChatRoomRequest(this.channel().id(), info);
        UUIDResponse r = (UUIDResponse)sendBlockingRequest(req);
        return r.response();
    }
    
    public boolean joinChat(UUID chatId) throws CommunicationException {
        Request req = new JoinChatRequest(chatId);
        BooleanResponse r = (BooleanResponse)sendBlockingRequest(req);
        return r.response();
    }

    void receiveChatMesage(UUID chatId, String userName, String message) {
        logger.info(this.userName + " recieved a chat message for chat " + chatId + " from " + userName + ":" + message);
    }
    
    public void sendChatMessage(UUID chatId, String message) throws CommunicationException {
        Request req = new ChatMessageRequest(chatId, message);
        sendRequest(req);
    }

    @Override
    public void disconnected(DisconnectReason reason, boolean error) {
        logger.info("Client was disconnected");
    }

    @Override
    public void connected() {
        logger.info("Client was connected");
    }

    public String userName() {
        return userName;
    }
    
}
