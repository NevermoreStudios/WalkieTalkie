package com.nevermore.walkietalkie.client;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.nevermore.walkietalkie.client.MainActivity;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

public class VoiceThread extends  Thread{
    private boolean running = true;
    private boolean recording = false;
    private boolean speaking = false;
    private AudioRecord input;
    private AudioTrack output;
    private MainActivity parent;
    private byte selected;
    private ArrayList<VoiceChannel> channels;
    private DatagramChannel ioSocket;

    public static final int PORT = 53730;
    public static final int SERVER_PORT = 53732;

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SPEAKING = 1;
    public static final int STATUS_RECORDING = 2;


    public VoiceThread(MainActivity ma) {
        parent = ma;
        init();
    }

    private short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    private byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }

    private boolean init() {
        input = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 44100);
        output = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 44100, AudioTrack.MODE_STREAM);
        channels = new ArrayList<VoiceChannel>();
        try {
            ioSocket = DatagramChannel.open();
            ioSocket.socket().bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        return true;
    }

    public void tcpMsg(ChatMessage msg) {
        VoiceChannel channel = getChannel();
        switch (msg.message.substring(0,5)) {
            case "STRSPK":
                channel.set((byte)STATUS_SPEAKING, msg.message.substring(6));
                startSpk();
                break;
            case "STPSPK":
                channel.set((byte)STATUS_AVAILABLE, null);
                stopSpk();
                break;
        }
    }

    public VoiceChannel getChannel() {
        return channels.get(selected);
    }

    public void changeChannel(byte id) {
        parent.ct.send(new ChatMessage(selected, "LEVCHN", parent.username));
        selected = id;
        parent.ct.send(new ChatMessage(selected, "JOICHN", parent.username));
    }

    public void leaveChannel() {
        parent.ct.send(new ChatMessage(selected, "LEVCHN", parent.username));
    }

    public boolean startRec() {
        recording = true;
        input.startRecording();
        parent.ct.send(new ChatMessage(selected, "STRSPK", parent.username));
        getChannel().setState(STATUS_RECORDING);
        return true;
    }

    public boolean stopRec() {
        recording = false;
        input.stop();
        parent.ct.send(new ChatMessage(selected, "STPSPK", parent.username));
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
        byte[] shorter = new byte[2];
        shorter = shortToBytes(val);
        buf.put(selected);
        buf.put(shorter);
        try {
            ioSocket.send(buf,new InetSocketAddress(InetAddress.getByName("255.255.255.255"),PORT));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
    }

    private void recieve() {
        ByteBuffer buf = ByteBuffer.allocate(3);
        byte[] shorter = new byte[2];
        short[] in = new short[1];
        try {
            ioSocket.receive(buf);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Error handling
        }
        if(buf.get(0) == selected) {
            shorter[0] = buf.get(1);
            shorter[1] = buf.get(2);
            in[0] = bytesToShort(shorter);
            output.write(in, 0, 1);
        }
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

}