package com.nevermore.walkietalkie.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {
    ServerThread st;
    VoiceServer vs;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        st = new ServerThread(this);
        st.start();
        System.out.println("wut");
        vs = new VoiceServer(this);
        vs.start();
        System.out.println("wut2");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        st.kill();
        vs.kill();
    }

}
