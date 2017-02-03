package org.betawares.jorre.example.chat.server;

import io.netty.channel.ChannelId;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.betawares.jorre.Server;
import org.betawares.jorre.Version;
import org.betawares.jorre.CommunicationException;

public class ChatServer extends Server {

    private static final Logger logger = Logger.getLogger(ChatServer.class);

    private final ConcurrentHashMap<UUID, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChannelId, String> users = new ConcurrentHashMap<>();

    private static final ChatServer instance = new ChatServer(new Version(1,0,0,"",""));


    private ChatServer(Version version) {
        super(version);
    }

    public static ChatServer instance() {
        return instance;
    }
    
    public boolean signIn(ChannelId channelId, String name) {
        return users.putIfAbsent(channelId, name) == null;
    }
    
    public UUID createChatRoom(ChannelId owner, String info) {
        ChatRoom chatRoom = new ChatRoom(owner, info);
        chatRooms.put(chatRoom.getChatId(), chatRoom);
        return chatRoom.getChatId();
    }

    @Override
    public void clientRemoved(ChannelId channelId) {
        users.remove(channelId);
    }

    @Override
    public void serverShutdown() {
        logger.info("Server was shutdown");
    }

    public ChatRoom getRoom(UUID chatId) {
        return this.chatRooms.get(chatId); 
    }
    
    public String getUserName(ChannelId id) {
        return this.users.get(id);
    }
    
    public void sendChatMessage(ChannelId channelId, UUID chatId, String userName, String message) throws CommunicationException {
        ChatMessageCallback msg = new ChatMessageCallback(chatId, userName, message);
        callback(channelId, msg);
    }
}
