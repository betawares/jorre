package org.betawares.jorre.example.chat.server;

import org.betawares.jorre.example.chat.common.ChatMessageCallback;
import io.netty.channel.ChannelId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.betawares.jorre.Server;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.Connection;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.example.chat.common.ChatRoomList;
import org.betawares.jorre.example.chat.common.ChatServerInterface;

public class ChatServer extends Server implements ChatServerInterface {

    private static final Logger logger = Logger.getLogger(ChatServer.class);

    private final ConcurrentHashMap<UUID, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChannelId, String> users = new ConcurrentHashMap<>();

    private static final ChatServer instance = new ChatServer(new Version(1,0,0,"",""));

    private ChatServer(Version version) {
        super(version);
    }

    public static void main(String[] args) {
        try {
            logger.info("Starting ChatServer ... ");
            instance.start(new Connection("", 17171));
            logger.info("Started ChatServer.");
        } catch (Exception ex) {
            logger.fatal("Error starting server:", ex);
        }
    }    
    
    public static ChatServer instance() {
        return instance;
    }
    
    @Override
    public boolean signIn(ChannelId channelId, String name) {
        logger.info("User signing in: " + name);
        return users.putIfAbsent(channelId, name) == null;
    }
    
    @Override
    public UUID createChatRoom(ChannelId owner, String info) {
        logger.info("Creating chat room: " + info);
        ChatRoom chatRoom = new ChatRoom(owner, info);
        chatRooms.put(chatRoom.getChatId(), chatRoom);
        return chatRoom.getChatId();
    }

    @Override
    public void clientWasRemoved(ChannelId channelId) {
        logger.info("Client removed: " + channelId);
        users.remove(channelId);
    }

    @Override
    public void disconnectClient(ChannelId channelId, DisconnectReason reason) throws CommunicationException {
        logger.info("Client disconnected: " + channelId);
        super.disconnectClient(channelId, reason);
    }

    @Override
    public void addClient(UUID clientId, ChannelId channelId) throws CommunicationException {
        logger.info("Client added: " + channelId);
        super.addClient(clientId, channelId);
    }
    
    @Override
    public void serverWasShutdown() {
        logger.info("Server was shutdown");
    }

    public ChatRoom getRoom(UUID chatId) {
        return this.chatRooms.getOrDefault(chatId, null); 
    }
    
    public String getUserName(ChannelId id) {
        return this.users.get(id);
    }
    
    public void sendChatMessage(ChannelId channelId, UUID chatId, String userName, String message) throws CommunicationException {
        ChatMessageCallback msg = new ChatMessageCallback(chatId, userName, message);
        callback(channelId, msg);
    }
    
    @Override
    public boolean joinChat(ChannelId channelId, UUID chatId) {
        ChatRoom chatRoom = getRoom(chatId);
        boolean response = false;
        if (chatRoom != null) {
            chatRoom.join(channelId, getUserName(channelId));
            response = true;
        } else {
            logger.trace("Chat to join not found - chatId: " + chatId + " userName: " + getUserName(channelId));
        }
        return response;
        
    }

    @Override
    public void handleChatMessage(ChannelId channelId, UUID chatId, String message) {
        ChatRoom room = getRoom(chatId);
        room.broadcast(getUserName(channelId), message);
    }

    @Override
    public ChatRoomList getRooms() {
        logger.info("Generating chat room list.");
        Map<UUID, String> list = new HashMap<>();
        for (UUID id: this.chatRooms.keySet()) {
            list.put(id, chatRooms.get(id).getInfo());
        }
        return new ChatRoomList(list);
    }
}
