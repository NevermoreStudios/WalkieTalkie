package com.nevermore.walkietalkie.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

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
        System.out.println(intent.getStringExtra(LoginActivity.EXTRA_USERNAME));
        System.out.println(intent.getStringExtra("adlsakjdkauidhaksljdhsa"));
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

    public Socket getConnection() {
        return connection;
    }

}
