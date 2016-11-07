package com.nevermore.walkietalkie.server;

import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    public static final int CHAT_PORT = 53731;
    public static final int VOICE_PORT = 53732;
    boolean running = true;
    ServerSocket server;
    DatagramSocket voiceServer;
    List<Client> clients = new ArrayList<>();
    List<ChatChannel> chatChannels = new ArrayList<>();
    List<VoiceChannel> voiceChannels = new ArrayList<>();

    public ServerThread() {
        initSocket();
        initChannels();
    }

    private void initChannels() {
        chatChannels.add(new ChatChannel((byte)0, "lol"));
    }

    private void initSocket() {
        try {
            server = new ServerSocket(CHAT_PORT);
            voiceServer = new DatagramSocket(VOICE_PORT);
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
        if(voiceChannels.get(id) != null) {
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
