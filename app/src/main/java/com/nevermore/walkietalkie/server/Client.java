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
    boolean running = true;

    public Client(Socket socket, ServerThread parent) {
        this.socket = socket;
        this.parent = parent;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
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
            System.out.println("jej imamo nick " + message);
            // Client is sending us a nickname, nickname them
            name = message;
            // Send channels to the client
            try {
                System.out.println(parent.serialize());
                out.println(parent.serialize());
                System.out.println("jason");
            } catch(JSONException e) {
                // TODO: Error handling
                e.printStackTrace();
            }
        } else {
            // Client is sending us an actual message
            System.out.println("paradajz " + message);
            int index = message.indexOf(Constants.DELIMITER);
            if(index == -1) {
                // TODO: Error handling
            } else {
                try {
                    byte channelID = Byte.parseByte(message.substring(0, index));
                    String msg = message.substring(index);
                    if(channelID < Constants.CHANNEL_DELIMITER) {
                        parent.sendMsg(name, channelID, msg);
                    } else {
                        parent.sendVoiceMsg(name, channelID, msg);
                    }
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

    public void kill(){ running = false;}

}
