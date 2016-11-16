package com.nevermore.walkietalkie.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatService extends Service {

    private IBinder binder = new ChatBinder();
    InetAddress serverAddress;
    public VoiceThread vt;
    public ChatThread ct;
    public ArrayList<String> members = new ArrayList<>();
    String username;
    Socket connection;

    public class ChatBinder extends Binder {
        ChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        username = intent.getStringExtra(Constants.EXTRA_USERNAME);
        ct = new ChatThread(this, intent.getStringExtra(Constants.EXTRA_SERVERIP));
        ct.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void initialize(ArrayList<VoiceChannel> channels) {
        vt = new VoiceThread(this, channels);
        vt.start();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(Constants.EXTRA_USERNAME,username);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void sendChatMsg(byte id, String message) {
        ct.sendMessage(id, message);
    }

    public void sendVoiceMsg(byte id, String sender, String message) {
        vt.tcpMsg(new ChatMessage(id, sender, message));
    }

    public void broadcastMessage(byte id, String sender, String msg) {
        Intent i = new Intent(Constants.RECEIVE_FILTER);
        i.putExtra(Constants.EXTRA_MESSAGE, msg);
        i.putExtra(Constants.EXTRA_SENDER, sender);
        i.putExtra(Constants.EXTRA_CHANNEL, id);
        sendBroadcast(i);
    }

    public Socket getConnection() {
        return connection;
    }

}
