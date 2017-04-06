package org.betawares.jorre.example.chat.protocol;

import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.ServerRequest;

public class ChatRoomListRequest extends ServerRequest<ChatServerInterface, ChatClientInterface> {

    @Override
    public void handle(ChatServerInterface server, ChannelHandlerContext ctx) {
        try {
            respond(new ChatRoomListResponse(server.getRooms()), ctx);
        } catch (CommunicationException ex) {
            server.handleException("Error handling ChatRoomListRequest", ex);
        }
    }
    
}
