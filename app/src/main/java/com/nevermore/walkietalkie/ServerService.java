package com.nevermore.walkietalkie;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ServerService extends Service {
    ServerThread st;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        st = new ServerThread();
        st.start();
        return START_STICKY;
    }

    @Nullable
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
