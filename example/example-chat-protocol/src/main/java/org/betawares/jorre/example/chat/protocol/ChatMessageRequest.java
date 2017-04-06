package org.betawares.jorre.example.chat.protocol;

import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import org.betawares.jorre.messages.requests.ServerRequest;

public class ChatMessageRequest extends ServerRequest<ChatServerInterface, ChatClientInterface> {

    private final String message;
    private final UUID chatId;
    
    public ChatMessageRequest(UUID chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    @Override
    public void handle(ChatServerInterface server, ChannelHandlerContext ctx) {
        server.handleChatMessage(ctx.channel().id(), chatId, message);        
    }
    
}
