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

    public VoiceServer(ServerService parr)
    {
     parrent=parr;
        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(SERVER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int PORT = 53730;
    public static final int SERVER_PORT = 53732;

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SPEAKING = 1;
    public static final int STATUS_RECORDING = 2;

    private boolean runing=true;
    private DatagramChannel ioSocket;
    ArrayList<VoiceChannel> channels= new ArrayList<VoiceChannel>();
    ServerService parrent;
    public void Kill()
    {
        runing = false;
    }

    private short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    private byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }

    public void tcpMsg(ChatMessage msg) {
        switch (msg.message.substring(0,5)) {
            case "STRSPK":
                for (String name:channels.get(msg.getChannel()).members)
                {
                parrent.st.sendMsg("",msg.getChannel(),msg.message,name);//not yet implemented
                }
                break;
            case "STPSPK":
                for (String name:channels.get(msg.getChannel()).members)
                {
                    parrent.st.sendMsg("",msg.getChannel(),msg.message,name);//not yet implemented
                }
                break;
            case "JOICHN":
                channels.get(msg.getChannel()).members.add(msg.getSender());
                parrent.st.Update();//not yet implemented
                break;

            case "LEVCHN":
                channels.get(msg.getChannel()).members.remove(msg.getSender());
                parrent.st.Update();//not yet implemented
                break;
        }
    }


    public void run()
    {

    }
}
