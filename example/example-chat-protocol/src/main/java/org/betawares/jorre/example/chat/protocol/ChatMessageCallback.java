package org.betawares.jorre.example.chat.protocol;

import java.util.UUID;
import org.betawares.jorre.messages.callback.ClientCallback;


public class ChatMessageCallback extends ClientCallback<ChatClientInterface> {
    
    private final UUID chatId;
    private final String userName;
    private final String message;

    public ChatMessageCallback(UUID chatId, String userName, String message) {
        this.chatId = chatId;
        this.userName = userName;
        this.message = message;
    }

    @Override
    public void handle(ChatClientInterface client) {
        client.receiveChatMesage(chatId, userName, message);
    }
    
}
