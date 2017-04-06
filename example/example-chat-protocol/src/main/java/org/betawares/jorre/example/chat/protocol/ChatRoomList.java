package org.betawares.jorre.example.chat.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatRoomList implements Serializable {
    
    private final Map<UUID, String> rooms = new HashMap<>();
    
    public ChatRoomList(Map<UUID, String> rooms) {
        this.rooms.putAll(rooms);
    }
    
    public Map<UUID, String> getRooms() {
        return rooms;
    }
}
