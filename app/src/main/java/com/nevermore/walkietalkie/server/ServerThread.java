package com.nevermore.walkietalkie.server;

import android.provider.Settings;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.VoiceChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    boolean running = true;
    ServerSocket server;
    List<Client> clients = new ArrayList<>();
    List<ChatChannel> chatChannels = new ArrayList<>();
    List<VoiceChannel> voiceChannels = new ArrayList<>();
    ServerService parent;

    public ServerThread(ServerService parent, ArrayList<ChatChannel> chatChannels, ArrayList<VoiceChannel> voiceChannels) {
        this.parent = parent;
        this.chatChannels = chatChannels;
        this.voiceChannels = voiceChannels;
        initSocket();
    }


    private void initSocket() {
        try {
            server = new ServerSocket(Constants.CHAT_SERVER_PORT);
        } catch(IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    public void sendMsg(String sender, byte id, String msg) {
        if(chatChannels.get(id) != null) {
            for(Client c : clients) {
                c.sendMsg(sender, id, msg);
            }
        }
    }

    private Client getClientByNickname(String nick) {
        for(Client c : clients) {
            if(nick.equals(c.getNick())) {
                return c;
            }
        }
        return null;
    }

    public void sendVoiceMsg(String sender, byte id, String msg) {
        if(voiceChannels.get(id - 1 - Constants.CHANNEL_DELIMITER) != null) {
            for(String nick : voiceChannels.get(id).members) {
                Client c = getClientByNickname(nick);
                if(c != null) {
                    c.sendMsg(sender, id, msg);
                }
            }
        }
    }

    public void kill() {
        running = false;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject ret = new JSONObject();
        for (ChatChannel c : chatChannels) {
            ret.put(c.getId() + "", c.getName());
        }
        for (VoiceChannel c : voiceChannels) {
            ret.put(c.getId() + "", c.getName());
        }
        return ret;
    }

    @Override
    public void run() {
        while(running) {
            try {
                Socket sock = server.accept();
                if(sock != null) {
                    System.out.println("neko se konektuje");
                    Client client = new Client(sock, this);
                    client.start();
                    clients.add(client);
                    System.out.println("neko se cese");
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Error handling
            }
        }
    }
}
