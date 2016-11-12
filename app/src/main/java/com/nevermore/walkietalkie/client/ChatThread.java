package com.nevermore.walkietalkie.client;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ChatThread extends Thread {
    
    public static final int PORT = 53729;
    private boolean running = true;
    private ArrayList<ChatChannel> channels = new ArrayList<>();
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
            // We are receiving the server status!
            try {
                JSONArray chans = new JSONArray(msg);
                ArrayList<VoiceChannel> vc = new ArrayList<>();
                for(int i = 0; i < chans.length(); ++i) {
                    JSONObject obj = chans.getJSONObject(i);
                    if(obj.getInt("id") < 128) {
                        // Text channel
                        channels.add(new ChatChannel((byte)obj.getInt("id"), obj.getString("name")));
                    } else {
                        vc.add(new VoiceChannel((byte)obj.getInt("id"), obj.getString("name")));
                    }
                }
                parent.createVoiceThread(vc);
            } catch(JSONException e) {
                e.printStackTrace();
                // TODO: Error handling
            }
        } else {
            // We are receiving a message
            String[] split = msg.split(Constants.DELIMITER);
            if(split.length == 3) {
                byte id = Byte.parseByte(split[0]);
                if(id < Constants.CHANNEL_DELIMITER) {
                    // Chat message
                    sendMessageUI(new ChatMessage(id, split[1], split[2]));
                } else {
                    // Voice message
                    parent.sendVoiceMsg(id, split[1], split[2]);
                }
            } else {
                // TODO: Error handling
            }
        }
    }

    private void sendMessageUI(ChatMessage msg) {
        // Sends the chat message to the UI
        // TODO: Implement
    }

}
