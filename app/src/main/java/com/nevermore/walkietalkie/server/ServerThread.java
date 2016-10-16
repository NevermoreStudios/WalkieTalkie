package com.nevermore.walkietalkie.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    public static final int SERVER_PORT = 53729;
    boolean running = true;
    ServerSocket server;
    List<Client> clients = new ArrayList<Client>();

    public ServerThread() {
        initSocket();
    }

    private void initSocket() {
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch(IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    public void sendMsg(int id, String msg) {
        // TODO: Implement after we implement channels
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
