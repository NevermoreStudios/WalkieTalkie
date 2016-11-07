package com.nevermore.walkietalkie.client;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ChatServiceConnection implements ServiceConnection {

    private boolean bound;
    ChatService service;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ChatService.ChatBinder binder = (ChatService.ChatBinder) service;
        bound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

}
