package com.nevermore.walkietalkie.server;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.VoiceChannel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    boolean running = true;
    ServerSocket server;
    DatagramSocket voiceServer;
    List<Client> clients = new ArrayList<>();
    List<ChatChannel> chatChannels = new ArrayList<>();
    List<VoiceChannel> voiceChannels = new ArrayList<>();
    ServerService parent;

    public ServerThread(ServerService parent) {
        this.parent = parent;
        initSocket();
        initChannels();
    }

    private void initChannels() {
        chatChannels.add(new ChatChannel((byte) 0, "lol"));
    }

    private void initSocket() {
        try {
            server = new ServerSocket(Constants.CHAT_SERVER_PORT);
            voiceServer = new DatagramSocket(Constants.VOICE_SERVER_PORT);
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
        // TODO: Implement after we implement channels
    }

    private Client getClientByNickname(String nick) {
        for(Client c : clients) {
            if(nick.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    public void sendVoiceMsg(String sender, byte id, String msg) {
        updateVoiceChannels();
        if(voiceChannels.get(id) != null) {
            for(String nick : voiceChannels.get(id).members) {
                Client c = getClientByNickname(nick);
                if(c != null) {
                    c.sendMsg(sender, id, msg);
                }
            }
        }
    }

    private void updateVoiceChannels() {
        voiceChannels = parent.vs.channels;
    }

    public void createChannel(byte id, String name){
        chatChannels.add(new ChatChannel(id,name));
    }

    public void deleteChannel(byte id, String name){
        chatChannels.remove(new ChatChannel(id, name));
    }

    public void kill() {
        running = false;
    }

    public JSONArray serialize() throws JSONException {
        JSONArray ret = new JSONArray();
        for (ChatChannel c : chatChannels) {
            ret.put(c.serialize());
        }
        for (VoiceChannel c : voiceChannels) {
            ret.put(c.serialize());
        }
        return ret;
    }

    @Override
    public void run() {
        while(running) {
            try {
                Socket sock = server.accept();
                if(sock != null) {
                    Client client = new Client(sock, this);
                    client.execute();
                    clients.add(client);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Error handling
            }
        }
    }
}
