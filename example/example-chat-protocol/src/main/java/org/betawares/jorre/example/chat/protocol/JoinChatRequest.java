package org.betawares.jorre.example.chat.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.BooleanResponse;

public class JoinChatRequest extends ServerRequest<ChatServerInterface, ChatClientInterface> {

    private final UUID chatId;
    
    public JoinChatRequest(UUID chatId) {
       this.chatId = chatId;
    }

    @Override
    public void handle(ChatServerInterface server, ChannelHandlerContext ctx) {
        ChannelId channelId = ctx.channel().id();
        boolean response = server.joinChat(channelId, chatId);
        try {        
            respond(new BooleanResponse(response), ctx);
        } catch (CommunicationException ex) {
            server.handleException("Error handling JoinChatRequest", ex);
        }
    }
        
}
