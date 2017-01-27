package org.betawares.jorre.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import java.util.UUID;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.Request;
import org.betawares.jorre.messages.responses.UUIDResponse;

public class CreateChatRoomRequest extends Request<ChatServer, ChatClient> {

    private final ChannelId owner;
    private final String info;
    
    public CreateChatRoomRequest(ChannelId id, String info) {
        this.owner = id;
        this.info = info;
    }

    @Override
    public void handle(ChatServer server, ChannelHandlerContext ctx) throws CommunicationException {
        UUID roomId = server.createChatRoom(owner, info);
        respond(new UUIDResponse(roomId), ctx);
    }
    
}
