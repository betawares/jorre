package org.betawares.jorre.example.chat.protocol;

import java.util.UUID;
import org.betawares.jorre.ClientInterface;

public interface ChatClientInterface extends ClientInterface {
    
    /**
     * This gets called from {@link SignInRequest} upon a successful sign-in.
     */
    public void signedIn();

    /**
     * 
     * This gets called when a chat message callback is received from the server
     * 
     * @param chatId
     * @param userName
     * @param message 
     */
    public void receiveChatMesage(UUID chatId, String userName, String message);

    /**
     * 
     * @param list 
     */
    public void displayRooms(ChatRoomList list);
    
}
