package com.nevermore.walkietalkie.models;

import java.util.ArrayList;
import java.util.List;

public class ChatChannel extends Channel {

    List<ChatMessage> messages = new ArrayList<>();

    public ChatChannel(byte id, String name) {
        super(id, name);
    }

}
