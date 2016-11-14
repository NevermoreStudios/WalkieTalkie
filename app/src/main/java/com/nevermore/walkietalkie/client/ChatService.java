package com.nevermore.walkietalkie.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ChatService extends Service {

    private IBinder binder = new ChatBinder();
    InetAddress serverAddress;
    VoiceThread vt;
    ChatThread ct;
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createChatThread() {
        ct = new ChatThread(this, connection);
    }

    public void createVoiceThread(ArrayList<VoiceChannel> channels) {
        vt = new VoiceThread(this, channels);
    }

    public void sendChatMsg(byte id, String message) {
        ct.sendMessage(id, message);
    }

    public void sendVoiceMsg(byte id, String sender, String message) {
        vt.tcpMsg(new ChatMessage(id, sender, message));
    }

    public Socket getConnection() {
        return connection;
    }

}
