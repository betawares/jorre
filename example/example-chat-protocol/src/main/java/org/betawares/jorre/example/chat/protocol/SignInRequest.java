package org.betawares.jorre.example.chat.protocol;

import io.netty.channel.ChannelHandlerContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.betawares.jorre.CommunicationException;
import org.betawares.jorre.DisconnectReason;
import org.betawares.jorre.messages.requests.ServerRequest;
import org.betawares.jorre.messages.responses.BooleanResponse;

public class SignInRequest extends ServerRequest<ChatServerInterface, ChatClientInterface> {


    private final String userName;
    
    public SignInRequest(String userName) {
        this.userName = userName;
    }
    
    /**
     * Here we handle a sign-in request on the server by responding with a {@link BooleanResonse}
     * indicating the result of calling {@code server.signIn}
     * 
     * The response handler is overridden to call the {@code client.signedIn} method which
     * notifies the client
     * 
     * @param server
     * @param ctx
     * @throws CommunicationException 
     */
    @Override
    public void handle(ChatServerInterface server, ChannelHandlerContext ctx) {
        try {
            respond(new BooleanResponse<ChatClientInterface>(server.signIn(ctx.channel().id(), userName)) {
                @Override
                public void handle(ChatClientInterface client) {
                    if (response()) {
                        client.signedIn();
                    }
                    else {
                        client.disconnect(DisconnectReason.Undefined, true);
                    }
                }
            }, ctx);
        } catch (CommunicationException ex) {
            server.handleException("Error handling SignInRequest", ex);
        }
        
    }

    
}
