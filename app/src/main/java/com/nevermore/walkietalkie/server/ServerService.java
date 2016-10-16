package com.nevermore.walkietalkie.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServerService extends Service {
    ServerThread st;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        st = new ServerThread();
        st.start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
    st.kill();
    }
}
