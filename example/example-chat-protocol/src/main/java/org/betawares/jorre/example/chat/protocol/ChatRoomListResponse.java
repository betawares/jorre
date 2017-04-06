package org.betawares.jorre.example.chat.protocol;

import org.betawares.jorre.messages.responses.ClientResponse;

public class ChatRoomListResponse extends ClientResponse<ChatClientInterface> {

    private ChatRoomList list;
    
    public ChatRoomListResponse(ChatRoomList list) {
        this.list = list;
    }
    
    @Override
    public void handle(ChatClientInterface client) {
        client.displayRooms(list);
    }
    
}
