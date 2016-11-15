package com.nevermore.walkietalkie.server;

import android.util.Xml;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class VoiceServer extends Thread{

    private boolean running = true;
    private DatagramChannel ioSocket;
    public ArrayList<VoiceChannel> channels = new ArrayList<>();
    private ServerService parent;

    public VoiceServer(ServerService parent, ArrayList<VoiceChannel> channels) {
        this.parent = parent;
        this.channels = channels;
        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().setReuseAddress(true);
            ioSocket.socket().bind(new InetSocketAddress(Constants.VOICE_SERVER_PORT));
            ioSocket.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        running = false;
    }

    private short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    private byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }

    public void tcpMsg(ChatMessage msg) {
        switch (msg.message.substring(0, 5)) {
            case "STRSPK":
                parent.st.sendVoiceMsg("", msg.getChannel(), msg.message);
                break;
            case "STPSPK":
                parent.st.sendVoiceMsg("", msg.getChannel(), msg.message);
                break;
            case "JOICHN":
                channels.get(msg.getChannel()).members.add(msg.getSender());
                parent.st.sendVoiceMsg(msg.getSender(), msg.getChannel(), "JOICHN");
                break;
            case "LEVCHN":
                channels.get(msg.getChannel()).members.remove(msg.getSender());
                parent.st.sendVoiceMsg(msg.getSender(), msg.getChannel(), "LEVCHN");
                break;
        }
    }

    @Override
    public void run() {
        InetSocketAddress ioa = null;
        ByteBuffer buf = ByteBuffer.allocate(4);
        ByteBuffer buff = ByteBuffer.wrap("ACK".getBytes());
        while(running) {
            try {
                ioa = (InetSocketAddress) ioSocket.receive(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(ioa != null) {
                System.out.println(buff.array().length);
                System.out.println(new String(buff.array()));
                String msg = new String(buf.array());
                System.out.println("nesto je stiglo");
                if(msg.equals("DISC")) {
                    System.out.println("neko nas je otkrio");
                    System.out.println(ioa.getAddress().toString());
                    try {
                        buff = ByteBuffer.wrap("ACK".getBytes());
                        ioSocket.send(buff,ioa);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
