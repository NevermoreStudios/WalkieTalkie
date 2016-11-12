package com.nevermore.walkietalkie.server;

import android.os.AsyncTask;

import com.nevermore.walkietalkie.Constants;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String name = null;
    ServerThread parent;
    boolean running = true; // TODO: Add .kill() method?

    public Client(Socket socket, ServerThread parent) {
        this.socket = socket;
        this.parent = parent;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
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
            // Send channels to the client
            try {
                this.out.println(parent.serialize());
            } catch(JSONException e) {
                // TODO: Error handling
                e.printStackTrace();
            }
        } else {
            // Client is sending us an actual message
            int index = message.indexOf(Constants.DELIMITER);
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
        this.out.println(id + Constants.DELIMITER + sender + Constants.DELIMITER + msg);
    }

    public String getName() {
        return name;
    }

}
