package org.betawares.jorre.example.chat.client;

import java.io.Console;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.betawares.jorre.Client;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.Connection;
import org.betawares.jorre.example.chat.common.ChatClientInterface;
import org.betawares.jorre.example.chat.common.ChatMessageRequest;
import org.betawares.jorre.example.chat.common.ChatRoomList;
import org.betawares.jorre.example.chat.common.ChatRoomListRequest;
import org.betawares.jorre.example.chat.common.CreateChatRoomRequest;
import org.betawares.jorre.example.chat.common.JoinChatRequest;
import org.betawares.jorre.example.chat.common.SignInRequest;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.BooleanResponse;
import org.betawares.jorre.messages.responses.UUIDResponse;


public class ChatClient extends Client implements ChatClientInterface {

    private static final Logger logger = Logger.getLogger(ChatClient.class);
    
    private final String userName;
    private final String host;
    private final int port;
    
    private UUID chatId = null;
       
    public ChatClient(Version version, String userName, String host, int port) {
        super(version);
        this.userName = userName;
        this.host = host;
        this.port = port;
    }
    
    public static void main(String[] args) throws CommunicationException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user name: ");
        String user = scanner.nextLine();
        System.out.print("Enter chat server url: ");
        String host = scanner.nextLine();
        System.out.print("Enter chat server port: ");
        int port = scanner.nextInt();
        
        logger.info("Connecting to " + host + " ... ");
        ChatClient client = new ChatClient(new Version(1,0,0,"",""), user, host, port);
        try {
            client.connect(new Connection(client.host, client.port));
        } catch (Exception ex) {
            logger.error("Error connecting to " + host + ":" + port, ex);
            System.exit(1);
        }
    }
    
    // send a SignInRequest to the server and wait for the response
    public boolean signIn() throws CommunicationException {
        ServerRequest req = new SignInRequest(userName);
        BooleanResponse r = (BooleanResponse)sendBlockingRequest(req);
        return r.response();
    }
    
    @Override
    public void signedIn() {
        logger.info(userName + " signedIn");
    }
    
    public UUID createChatRoom(String info) throws CommunicationException {
        ServerRequest req = new CreateChatRoomRequest(this.channel().id(), info);
        UUIDResponse r = (UUIDResponse)sendBlockingRequest(req);
        return r.response();
    }
    
    public void joinChat(UUID chatId) throws CommunicationException {
        ServerRequest req = new JoinChatRequest(chatId);
        BooleanResponse r = (BooleanResponse)sendBlockingRequest(req);
        if (r.response())
            this.chatId = chatId;
    }

    @Override
    public void receiveChatMesage(UUID chatId, String userName, String message) {
        System.out.printf("%s recieved a chat message for chat %s from %s\n%s\n", this.userName, chatId, userName, message);
    }
    
    public void sendChatMessage(UUID chatId, String message) throws CommunicationException {
        ServerRequest req = new ChatMessageRequest(chatId, message);
        sendMessage(req);
    }
    
    public void sendChatRoomListRequest() throws CommunicationException {
        ServerRequest req = new ChatRoomListRequest();
        sendBlockingRequest(req);
    }

    @Override
    public void disconnected(DisconnectReason reason, boolean error) {
        logger.info("Client was disconnected");
        System.exit(error ? 1 : 0);
    }

    @Override
    public void connected() {
        Scanner scanner = new Scanner(System.in);
        logger.info("Client was connected");
        logger.info("Signing-in to " + host + ":" + port + " as " + userName);
        try {
            if (!signIn()) {
                logger.error("Signin failed");
                System.exit(1);
            }
        } catch (CommunicationException ex) {
            logger.error("Error signing in", ex);
            System.exit(1);
        }
        
        try {
            System.out.print(">");
            while (scanner.hasNext()) {
                String line = scanner.next();
                if (line.startsWith("join:")) {
                    joinChat(UUID.fromString(line.replaceFirst("join:", "")));
                }
                else if (line.startsWith("send:")) {
                    sendChatMessage(chatId, line.replaceFirst("send:", ""));
                }
                else if (line.startsWith("list:")) {
                    sendChatRoomListRequest();
                }
                else if (line.startsWith("create:")) {
                    createChatRoom(line.replaceFirst("create:", ""));
                }
                else if (line.startsWith("quit:")) {
                    break;
                }
                if (chatId != null)
                    System.out.print(chatId.toString());
                System.out.print(">");
            }
        }
        catch (CommunicationException ex) {
            logger.error("Error", ex);
            System.exit(1);
        }
        disconnect();
    }

    @Override
    public void displayRooms(ChatRoomList rooms) {
        Map<UUID, String> list = rooms.getRooms();
        Console console = System.console();
        for (UUID id: list.keySet()) {
            System.out.printf("id: %s ==> name: %s\n", id, list.get(id));
        }
    }
    
}
