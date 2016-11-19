package com.nevermore.walkietalkie.server;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class VoiceServer extends Thread{

    private boolean running = true;
    private DatagramChannel ioSocket;
    public ArrayList<VoiceChannel> channels = new ArrayList<>();
    private ServerService parent;
    int status=Constants.STATUS_AVAILABLE;

    public VoiceServer(ServerService parent, ArrayList<VoiceChannel> channels) {
        this.parent = parent;
        this.channels = channels;
        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().setReuseAddress(true);
            ioSocket.socket().bind(new InetSocketAddress(Constants.VOICE_SERVER_PORT));
            ioSocket.configureBlocking(false);
            ioSocket.socket().setBroadcast(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        running = false;
    }

    public void tcpMsg(ChatMessage msg) {
        switch (msg.message.substring(0, 6)) {
            case "STRSPK":
                status = Constants.STATUS_SPEAKING;
                parent.st.sendVoiceMsg(msg.getSender(), msg.getChannel(), msg.message);
                break;
            case "STPSPK":
                status = Constants.STATUS_AVAILABLE;
                parent.st.sendVoiceMsg(msg.getSender(), msg.getChannel(), msg.message);
                break;
            case "JOICHN":
                channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).members.add(msg.getSender());
                String data = "JOICHN" + Constants.VOICE_DELIMITER + status;
                for(String a : channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).members) {
                    data += Constants.VOICE_DELIMITER + a;
                }
                parent.st.sendVoiceMsg(msg.getSender(), msg.getChannel(), data);
                break;
            case "LEVCHN":
                parent.st.sendVoiceMsg(msg.getSender(), msg.getChannel(), "LEVCHN");
                channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).members.remove(msg.getSender());
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
                String msg = new String(buf.array());
                if(msg.equals("DISC")) {
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
