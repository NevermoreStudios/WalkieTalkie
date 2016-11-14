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

public class ChatThread extends Thread {

    private boolean running = true;
    private String serverAddress;
    private ArrayList<ChatChannel> channels = new ArrayList<>();
    private Socket socket;
    private ChatService parent;
    private BufferedReader in;
    private PrintWriter out;

    public ChatThread(ChatService parent, String serverAddress) {
        Log.d("ChatThread", "Started chat thread");
        this.parent = parent;
        this.serverAddress = serverAddress;
    }

    public void kill() {
        running = false;
    }

    public void run() {
        try {
            socket = new Socket(InetAddress.getByName(serverAddress.substring(1)), Constants.CHAT_SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            Log.d("ChatThread", "Instantiated output and input");
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
                Log.d("ChatThread", "Yay server status");
                channels = new ArrayList<>();
                JSONArray chans = new JSONArray(msg);
                Log.d("ChatThread", chans.toString());
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
                Log.d("ChatThread", "RIP JSON");
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

    public void sendMessage(byte id, String message) {
        out.println(id + Constants.DELIMITER + message);
    }

    public ArrayList<ChatChannel> getChannels() {
        return channels;
    }

}
