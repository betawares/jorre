package org.betawares.jorre.example;

import io.netty.channel.ChannelHandlerContext;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.messages.requests.Request;
import org.betawares.jorre.messages.responses.BooleanResponse;

public class SignInRequest extends Request<ChatServer, ChatClient> {


    private final String userName;
    
    public SignInRequest(String userName) {
        this.userName = userName;
    }
    
    @Override
    public void handle(ChatServer server, ChannelHandlerContext ctx) throws CommunicationException {
        respond(new BooleanResponse<ChatClient>(server.signIn(ctx.channel().id(), userName)) {
          @Override
          public void handle(ChatClient client) {
             client.signedIn();
          }
        }, ctx);
        
    }

    
}
