package com.nevermore.walkietalkie.client;

import android.content.Intent;
import android.os.AsyncTask;

import com.nevermore.walkietalkie.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DiscoveryService extends AsyncTask<String, Void, String> {

    String name;
    LoginActivity parent;

    public DiscoveryService(String name, LoginActivity parent) {
        this.name = name;
        this.parent = parent;
    }


    protected String doInBackground(String... name) {
        InetSocketAddress ina = null;
        ByteBuffer buf = ByteBuffer.allocate(3);
        ByteBuffer buff = ByteBuffer.wrap("DISC".getBytes());
        try {
            DatagramChannel ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(Constants.DISCOVERY_PORT));
            ioSocket.socket().setBroadcast(true);
            ioSocket.send(buff, new InetSocketAddress(Constants.broadCast, Constants.VOICE_SERVER_PORT));
            ina = (InetSocketAddress) ioSocket.receive(buf);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        if(ina != null) {
            String text = new String(buf.array());
            if(text.equals("ACK")) {
                return ina.getAddress().toString();
            }
        }
        return null;
    }

    protected void onPostExecute(String result) {
        if(result != null) {
            Intent data = new Intent(parent, ChatService.class);
            data.putExtra(Constants.EXTRA_USERNAME, name);
            data.putExtra(Constants.EXTRA_SERVERIP, result.substring(1));
            parent.startService(data);
        }
    }

}
