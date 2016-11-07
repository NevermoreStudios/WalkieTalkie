package com.nevermore.walkietalkie.server;

import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class VoiceServer extends Thread{

    public VoiceServer(ServerService parr) {
        parent = parr;
        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(SERVER_PORT));
            ioSocket.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int PORT = 53730;
    public static final int SERVER_PORT = 53732;

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SPEAKING = 1;
    public static final int STATUS_RECORDING = 2;

    private boolean running = true;
    private DatagramChannel ioSocket;
    private ArrayList<VoiceChannel> channels = new ArrayList<>();
    private ServerService parent;

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

}
