package org.betawares.jorre.example.chat.protocol;

import io.netty.channel.ChannelId;
import java.util.UUID;
import org.betawares.jorre.ServerInterface;


public interface ChatServerInterface extends ServerInterface {
    
    public boolean signIn(ChannelId channelId, String name);
    
    public UUID createChatRoom(ChannelId owner, String info);
    
    public boolean joinChat(ChannelId channelId, UUID chatId);

    public void handleChatMessage(ChannelId channelId, UUID chatId, String message);
    
    public ChatRoomList getRooms();
    
}
