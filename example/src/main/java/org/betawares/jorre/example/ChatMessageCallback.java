package org.betawares.jorre.example;

import java.util.UUID;
import org.betawares.jorre.messages.callback.ClientCallback;


public class ChatMessageCallback extends ClientCallback<ChatClient> {
    
    private final UUID chatId;
    private final String userName;
    private final String message;

    public ChatMessageCallback(UUID chatId, String userName, String message) {
        this.chatId = chatId;
        this.userName = userName;
        this.message = message;
    }

    @Override
    public void handle(ChatClient client) {
        client.receiveChatMesage(chatId, userName, message);
    }
    
}
