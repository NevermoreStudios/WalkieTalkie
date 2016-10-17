package com.nevermore.walkietalkie.client;

import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.ChatMessage;

import java.util.ArrayList;

public class ChatThread extends Thread {
    
    public static final int PORT = 53729;
    boolean running = true;
    ArrayList<ChatChannel> channels;

    public ChatMessage recieve() {
        // TODO: Implement
        return new ChatMessage((byte)0, "", "");
    }

    public ChatThread() {
        // TODO: Implement
    }

    public void send(ChatMessage msg) {
        // TODO: Implement
    }

    public void kill() {
        running = false;
    }

    public void run() {
        while(running) {
            // TODO: Implement
        }
    }
}
