package com.nevermore.walkietalkie.client;

import android.util.Log;

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
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatThread extends Thread {

    private boolean running = true;
    private String serverAddress;
    private ArrayList<ChatChannel> channels;
    private Socket socket;
    private ChatService parent;
    private BufferedReader in;
    private PrintWriter out;

    public ChatThread(ChatService parent, String serverAddress) {
        this.parent = parent;
        this.serverAddress = serverAddress;
    }

    public void kill() {
        running = false;
    }

    public void run() {
        try {
            socket = new Socket(InetAddress.getByName(serverAddress), Constants.CHAT_SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(parent.username);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
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
                channels = new ArrayList<>();
                JSONObject chans = new JSONObject(msg);
                ArrayList<VoiceChannel> vc = new ArrayList<>();
                Iterator<String> keys = chans.keys();
                while(keys.hasNext()) {
                    byte key = Byte.parseByte(keys.next());
                    if(key < Constants.CHANNEL_DELIMITER) {
                        // Text channel
                        channels.add(new ChatChannel(key, chans.getString(key + "")));
                    } else {
                        vc.add(new VoiceChannel(key, chans.getString(key + "")));
                    }
                }
                parent.initialize(vc);
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
                    parent.broadcastMessage(id, split[1], split[2]);
                } else {
                    // Voice message
                    parent.sendVoiceMsg(id, split[1], split[2]);
                }
            } else {
                // TODO: Error handling
            }
        }
    }

    public void sendMessage(byte id, String message) {
        out.println(id + Constants.DELIMITER + message);
    }

    public ArrayList<ChatChannel> getChannels() {
        return channels;
    }

}
