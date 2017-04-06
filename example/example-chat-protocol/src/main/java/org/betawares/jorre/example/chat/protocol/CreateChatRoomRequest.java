package org.betawares.jorre.example.chat.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import java.util.UUID;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.UUIDResponse;

public class CreateChatRoomRequest extends ServerRequest<ChatServerInterface, ChatClientInterface> {

    private final ChannelId owner;
    private final String info;
    
    public CreateChatRoomRequest(ChannelId id, String info) {
        this.owner = id;
        this.info = info;
    }

    @Override
    public void handle(ChatServerInterface server, ChannelHandlerContext ctx) {
        UUID roomId = server.createChatRoom(owner, info);
        try {
            respond(new UUIDResponse(roomId), ctx);
        } catch (CommunicationException ex) {
            server.handleException("Error handling CreateChatRoomRequest", ex);
        }
    }
    
}
