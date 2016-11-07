package com.nevermore.walkietalkie.server;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    Socket socket;
    BufferedReader in;
    String name = null;
    ServerThread parent;
    boolean running = true; // TODO: Add .kill() method?
    public static char DELIMITER = 65535;

    public Client(Socket socket, ServerThread parent) {
        this.socket = socket;
        this.parent = parent;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
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
        return null;
    }

    private void handleMessage(String message) {
        if(name == null) {
            // Client is sending us a nickname, nickname them
            name = message;
        } else {
            // Client is sending us an actual message
            int index = message.indexOf(DELIMITER);
            if(index == -1) {
                // TODO: Error handling
            } else {
                try {
                    byte channelID = Byte.parseByte(message.substring(0, index));
                    String msg = message.substring(index);
                    parent.sendMsg(name, channelID, msg);
                } catch(Exception e) {
                    e.printStackTrace();
                    // TODO: Error handling
                }
            }
        }
    }

    public void sendMsg(String sender, byte id, String msg) {
        // TODO: Implement
    }

    public String getName() {
        return name;
    }

}
