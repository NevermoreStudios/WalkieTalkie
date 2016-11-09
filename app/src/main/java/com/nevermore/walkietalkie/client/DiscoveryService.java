package com.nevermore.walkietalkie.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DiscoveryService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Implement
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
