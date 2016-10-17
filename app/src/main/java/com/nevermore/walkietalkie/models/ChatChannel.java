package com.nevermore.walkietalkie.models;

import java.util.ArrayList;

public class ChatChannel extends Channel {

    ArrayList<ChatMessage> messages;

    public ChatChannel(byte id, String name) {
        super(id, name);
    }

}
