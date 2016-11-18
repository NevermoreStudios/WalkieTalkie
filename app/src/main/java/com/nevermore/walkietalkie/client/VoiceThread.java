package com.nevermore.walkietalkie.client;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.provider.Settings;

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

public class VoiceThread extends Thread {

    private boolean running = true;
    private boolean recording = false;
    private boolean speaking = false;
    private AudioRecord input;
    private AudioTrack output;
    private ChatService parent;
    private byte selected=-1;
    private ArrayList<VoiceChannel> channels;
    private DatagramChannel ioSocket;
    private int i=0;

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SPEAKING = 1;
    public static final int STATUS_RECORDING = 2;


    public VoiceThread(ChatService parent, ArrayList<VoiceChannel> channels) {
        this.parent = parent;
        this.channels = channels;
        init();
    }


    private boolean init() {
        try {
            input = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize ( 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));
            output = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize ( 44100,AudioFormat.CHANNEL_OUT_MONO , AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
            ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(Constants.VOICE_PORT));
            ioSocket.configureBlocking(false);
            ioSocket.socket().setBroadcast(true);
        } catch (IOException e) {
            e.printStackTrace();
            input.release();
            output.release();
            // TODO: Error handling
        }
        return true;
    }

    public void tcpMsg(ChatMessage msg) {
        String s = msg.message.substring(0, 6);
        if (s.equals("STRSPK")) {
            System.out.println(msg.message.substring(6));
            System.out.println(parent.username);
            if(msg.message.substring(6)!=parent.username){
            getChannel().set((byte) STATUS_SPEAKING, msg.message.substring(6));
            startSpk();
            }
        } else if (s.equals("STPSPK")) {
            getChannel().set((byte) STATUS_AVAILABLE, null);
            stopSpk();
        }else if (s.equals("JOICHN") && msg.getSender() != parent.username) {
            channels.get(msg.getChannel()-Constants.CHANNEL_DELIMITER-1).members.add(msg.getSender());
        }else if (s.equals("LEVCHN") && msg.getSender() != parent.username) {
            channels.get(msg.getChannel()-Constants.CHANNEL_DELIMITER-1).members.remove(msg.getSender());
        }

    }

    public VoiceChannel getChannel() {
        return channels.get(selected-1);
    }

    public void changeChannel(byte id) {
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "LEVCHN");
        channels.get(selected-1).members.remove(parent.username);
        selected = id;
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "JOICHN");
        channels.get(selected-1).members.add(parent.username);
    }

    public void joinChannel(byte id) {
        selected = id;
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "JOICHN");
        channels.get(selected-1).members.add(parent.username);
    }

    public void leaveChannel() {
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "LEVCHN");
        channels.get(selected-1).members.remove(parent.username);
    }

    public boolean startRec() {
        recording = true;
        input.startRecording();
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "STRSPK");
        getChannel().setState(STATUS_RECORDING);
        parent.broadcastStatus(STATUS_RECORDING);
        return true;
    }

    public boolean stopRec() {
        recording = false;
        input.stop();
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "STPSPK");
        getChannel().setState(STATUS_AVAILABLE);
        parent.broadcastStatus(STATUS_AVAILABLE);
        return true;
    }

    private boolean startSpk() {
        System.out.println("play");
        speaking = true;
        output.play();
        return true;
    }

    private boolean stopSpk() {
        speaking = false;
        System.out.println("stop");
        output.stop();
        output.flush();
        return true;
    }

    private void send(short[] val) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        //buf.put(selected);
        //System.out.println(buf.array()[0]);
        for(int i = 0;i<1024;i++)
        {
            buf.putShort(val[i]);
            System.out.print(val[i]+" ");
        }
        System.out.println();
        try {
            ioSocket.send(buf, new InetSocketAddress(Constants.broadCast, Constants.VOICE_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    private void recieve() {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        SocketAddress ina=null;
        short[] in = new short[1024];
        try {
           ina= ioSocket.receive(buf);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        if(ina != null){
            //System.out.println(buf.array()[0]);
        //if(buf.get(0) == selected) {
            try{
            for(int i =0;i<1024;i++)
            {
                in[i]=buf.getShort();
                System.out.print(in[i]);
            }
                System.out.println();
            System.out.println(in.length);
            i=output.write(in,i, 1024);
                System.out.println(i);
            }
            catch (Exception e){e.printStackTrace();}

        }//}
    }

    public void kill() {
        running = false;
    }

    public void run() {
        short buff[] = new short[1024];
        while(running) {
            if(recording) {
                input.read(buff, 0, 1024);
                send(buff);
            } else if(speaking) {
                recieve();
            }
            try {
                Thread.sleep(24);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<VoiceChannel> getChannels() {
        return channels;
    }

}