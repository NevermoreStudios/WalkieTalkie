package com.nevermore.walkietalkie.client;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.nevermore.walkietalkie.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.DatagramChannel;

public class DiscoveryService extends AsyncTask<String, Void, String> {

    private DatagramChannel ioSocket;
    String name;
    LoginActivity parent;

    public DiscoveryService(String name,LoginActivity parr)
    {
        this.name=name;
        parent=parr;

    }


    protected String doInBackground(String... name) {

        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(Constants.DISCOVERY_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buf = ByteBuffer.allocate(3);
        ByteBuffer buff = ByteBuffer.wrap("DISC".getBytes());
        InetSocketAddress ina = null;
        try {
            ioSocket.send(buff,new InetSocketAddress(InetAddress.getByName("255.255.255.255"),Constants.VOICE_SERVER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("waiting");
        try {

            ina = (InetSocketAddress) ioSocket.receive(buf);

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        System.out.println("recieved");
        if(ina != null) {
            String text = new String(buf.array());
            if(text.equals("ACK")) {
                System.out.println(ina.getAddress().toString());
                return ina.getAddress().toString();
            }
        }
        return null;
    }

    protected void onPostExecute(String result) {
        Intent data = new Intent(parent, ChatService.class);
        data.putExtra(Constants.EXTRA_USERNAME, name);
        data.putExtra(Constants.EXTRA_SERVERIP, result);
        parent.startService(data);
    }

}
