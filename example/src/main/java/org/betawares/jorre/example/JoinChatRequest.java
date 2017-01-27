package org.betawares.jorre.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import java.util.UUID;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.Request;
import org.betawares.jorre.messages.responses.BooleanResponse;

public class JoinChatRequest extends Request<ChatServer, ChatClient> {

    private final UUID chatId;
    
    public JoinChatRequest(UUID chatId) {
       this.chatId = chatId;
    }

    @Override
    public void handle(ChatServer server, ChannelHandlerContext ctx) throws CommunicationException {
        ChatRoom chatRoom = server.getRoom(chatId);
        boolean response = false;
        ChannelId channelId = ctx.channel().id();
        if (chatRoom != null) {
            chatRoom.join(channelId, server.getUserName(channelId));
            response = true;
        } else {
            logger.trace("Chat to join not found - chatId: " + chatId + " userName: " + server.getUserName(channelId));
        }
        respond(new BooleanResponse(response), ctx);
    }
        
}
