package com.nevermore.walkietalkie.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DiscoveryService extends Service {

    private DatagramChannel ioSocket;

    public static final String EXTRA_USERNAME = "com.nevermore.walkietalkie.extra.username";
    public static final String EXTRA_SERVERIP = "com.nevermore.walkietalkie.extra.serverip";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(53735));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buf = ByteBuffer.allocate(3);
        InetSocketAddress ina = null;
        try {
            ina = (InetSocketAddress) ioSocket.receive(buf);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        if(ina != null) {
            String text = new String(buf.array());
            if(text.equals("ACK")) {
                Intent data = new Intent(this, ChatService.class);
                data.putExtra(EXTRA_USERNAME,intent.getStringExtra(LoginActivity.EXTRA_USERNAME));
                data.putExtra(EXTRA_SERVERIP,ina.getAddress().toString() );
                startService(data);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
