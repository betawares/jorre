package org.betawares.jorre.example;

import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.Request;

public class ChatMessageRequest extends Request<ChatServer, ChatClient> {

    private final String message;
    private final UUID chatId;
    
    public ChatMessageRequest(UUID chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    @Override
    public void handle(ChatServer server, ChannelHandlerContext ctx) throws CommunicationException {
        ChatRoom room = server.getRoom(chatId);
        room.broadcast(server.getUserName(ctx.channel().id()), message);
    }
    
}
