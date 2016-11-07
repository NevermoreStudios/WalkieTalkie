package com.nevermore.walkietalkie.client;

import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.ChatMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ChatThread extends Thread {
    
    public static final int PORT = 53729;
    private boolean running = true;
    private ArrayList<ChatChannel> channels;
    private Socket socket;
    private ChatService parent;
    private BufferedReader in;

    public ChatThread(ChatService parent, Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    public ChatMessage recieve() {
        // TODO: Implement
        return new ChatMessage((byte)0, "", "");
    }

    public void send(ChatMessage msg) {
        // TODO: Implement
    }

    public void kill() {
        running = false;
    }

    public void run() {
        while(running) {
            try {
                String msg = in.readLine();
                if(msg != null) {
                    handleMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Error handling
            }
        }
    }

    private void handleMessage(String msg) {
        if(channels == null) {
            // We are recieving the server status!

        }
    }

}
