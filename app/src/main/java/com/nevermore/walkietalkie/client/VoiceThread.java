package com.nevermore.walkietalkie.client;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
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
    private byte selected;
    private ArrayList<VoiceChannel> channels;
    private DatagramChannel ioSocket;

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
        } catch (IOException e) {
            e.printStackTrace();
            input.release();
            output.release();
            // TODO: Error handling
        }
        return true;
    }

    public void tcpMsg(ChatMessage msg) {
        VoiceChannel channel = getChannel();
        String s = msg.message.substring(0, 5);
        if (s.equals("STRSPK")) {
            if(msg.message.substring(6)!=parent.username){
            channel.set((byte) STATUS_SPEAKING, msg.message.substring(6));
            startSpk();
            }
        } else if (s.equals("STPSPK")) {
            channel.set((byte) STATUS_AVAILABLE, null);
            stopSpk();
        }else if (s.equals("JOICHN") && msg.getSender() != parent.username) {
            channels.get(msg.getChannel()).members.add(msg.getSender());
        }else if (s.equals("LEVCHN") && msg.getSender() != parent.username) {
            channels.get(msg.getChannel()).members.remove(msg.getSender());
        }

    }

    public VoiceChannel getChannel() {
        return channels.get(selected);
    }

    public void changeChannel(byte id) {
        parent.ct.sendMessage(selected, "LEVCHN");
        channels.get(selected).members.remove(parent.username);
        selected = id;
        parent.ct.sendMessage(selected, "JOICHN");
        channels.get(selected).members.add(parent.username);
    }

    public void leaveChannel() {
        parent.ct.sendMessage(selected, "LEVCHN");
        channels.get(selected).members.remove(parent.username);
    }

    public boolean startRec() {
        recording = true;
        input.startRecording();
        parent.ct.sendMessage(selected, "STRSPK");
        getChannel().setState(STATUS_RECORDING);
        return true;
    }

    public boolean stopRec() {
        recording = false;
        input.stop();
        parent.ct.sendMessage(selected, "STPSPK");
        getChannel().setState(STATUS_AVAILABLE);
        return true;
    }

    private boolean startSpk() {
        speaking = true;
        output.play();
        return true;
    }

    private boolean stopSpk() {
        speaking = false;
        output.stop();
        output.flush();
        return true;
    }

    private void send(short val) {
        ByteBuffer buf = ByteBuffer.allocate(3);
        buf.put(selected);
        buf.putShort(val);
        try {
            ioSocket.send(buf, new InetSocketAddress(InetAddress.getByName("255.255.255.255"), Constants.VOICE_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    private void recieve() {
        ByteBuffer buf = ByteBuffer.allocate(3);
        SocketAddress ina=null;
        byte[] shorter = new byte[2];
        short[] in = new short[1];
        try {
           ina= ioSocket.receive(buf);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        if(ina != null){
        if(buf.get(0) == selected) {
            in[0] = buf.getShort();
            output.write(in, 0, 1);
        }}
    }

    public void kill() {
        running = false;
    }

    public void run() {
        short buff[] = new short[1];
        while(running) {
            if(recording) {
                input.read(buff, 0, 1);
                send(buff[0]);
            } else if(speaking) {
                recieve();
            }
        }
    }

    public ArrayList<VoiceChannel> getChannels() {
        return channels;
    }

}