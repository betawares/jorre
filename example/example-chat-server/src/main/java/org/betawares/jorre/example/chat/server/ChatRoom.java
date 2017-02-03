package org.betawares.jorre.example.chat.server;



import io.netty.channel.ChannelId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.betawares.jorre.CommunicationException;

public class ChatRoom {

    private static final Logger logger = Logger.getLogger(ChatRoom.class);

    private final ConcurrentHashMap<ChannelId, String> chatters = new ConcurrentHashMap<>();
    private final ChannelId owner;
    private final UUID chatId;
    private final Date createTime;
    private final String info;

    ChatRoom(ChannelId owner, String info) {
        chatId = UUID.randomUUID();
        this.owner = owner;
        this.createTime = new Date();
        this.info = info;
    }
    
    public UUID getChatId() {
        return chatId;
    }
    
    public void join(ChannelId channelId, String userName) {
        chatters.put(channelId, userName);
        broadcast(userName, userName + " has joined");
        logger.trace(userName + " joined chat " + chatId);
    }

    public void broadcast(String userName, String message) {
        if (!message.isEmpty()) {
            logger.trace("Broadcasting '" + message + "' for " + chatId);
            for (ChannelId channelId: chatters.keySet()) {
                try {
                    ChatServer.instance().sendChatMessage(channelId, chatId, userName, message);
                } catch (CommunicationException ex) {
                    logger.error("Error sending chat message", ex);
                }
            }
        }
    }    
}
