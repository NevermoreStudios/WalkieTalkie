package com.nevermore.walkietalkie.client;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.models.ChatMessage;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class VoiceThread extends Thread {

    private boolean running = true;
    private boolean recording = false;
    private AudioRecord input;
    private AudioTrack output;
    private ChatService parent;
    private byte selected = -1;
    private ArrayList<VoiceChannel> channels;
    private DatagramChannel ioSocket;

    public VoiceThread(ChatService parent, ArrayList<VoiceChannel> channels) {
        this.parent = parent;
        this.channels = channels;
        init();
    }


    private boolean init() {
        try {
            input = new AudioRecord(MediaRecorder.AudioSource.MIC, Constants.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, Constants.SAMPLE_RATE*2);
            output = new AudioTrack(AudioManager.STREAM_MUSIC, Constants.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, Constants.SAMPLE_RATE*2, AudioTrack.MODE_STREAM);
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
        if (msg.getMessage().equals("STRSPK")) {
            if(!msg.getSender().equals(parent.username)){
                getChannel().set((byte) Constants.STATUS_SPEAKING, msg.getSender());
                parent.broadcastStatus(Constants.STATUS_SPEAKING);
                startSpk();
            }
        } else if (msg.getMessage().equals("STPSPK")) {
            if(!msg.getSender().equals(parent.username)){
                getChannel().set((byte) Constants.STATUS_AVAILABLE, null);
                parent.broadcastStatus(Constants.STATUS_AVAILABLE);
                stopSpk();
            }
        } else if (msg.getMessage().substring(0, 6).equals("JOICHN")) {
            String[] data = msg.getMessage().substring(7).split(Constants.VOICE_DELIMITER);
            try {
                channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).setState(Integer.parseInt(data[0]));
                channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).members.clear();
                for (int i = 1; i < data.length; ++i) {
                    channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).members.add(data[i]);
                }
                updateMembers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (msg.getMessage().equals("LEVCHN")) {
            channels.get(msg.getChannel() - Constants.CHANNEL_DELIMITER - 1).members.remove(msg.getSender());
            updateMembers();
        }
    }

    private void updateMembers() {
        parent.broadcastMembers();
    }

    public VoiceChannel getChannel() {
        return channels.get(selected - 1);
    }

    public void changeChannel(byte id) {
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "LEVCHN");
        selected = id;
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "JOICHN");
    }

    public void joinChannel(byte id) {
        selected = id;
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "JOICHN");
    }

    public void leaveChannel() {
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "LEVCHN");
        selected = -1;
    }

    public boolean startRec() {
        recording = true;
        input.startRecording();
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "STRSPK");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getChannel().setState(Constants.STATUS_RECORDING);
        parent.broadcastStatus(Constants.STATUS_RECORDING);
        return true;
    }

    public boolean stopRec() {
        recording = false;
        input.stop();
        try {
            Thread.sleep(1690);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        parent.ct.sendMessage((byte)(selected+Constants.CHANNEL_DELIMITER), "STPSPK");
        getChannel().setState(Constants.STATUS_AVAILABLE);
        parent.broadcastStatus(Constants.STATUS_AVAILABLE);
        return true;
    }

    private boolean startSpk() {
        output.play();
        return true;
    }

    private boolean stopSpk() {
        output.pause();
        output.flush();
        output.stop();
        return true;
    }

    private void send() {
        try {
            short[] arr =new short[Constants.SAMPLES];
            arr[0]=selected;
            input.read(arr,1,arr.length-1);
            ByteBuffer buf = ByteBuffer.allocate(Constants.SAMPLES*2);
            buf.asShortBuffer().put(arr);
            ioSocket.send(buf,new InetSocketAddress(Constants.broadCast, Constants.VOICE_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recieve() {
        try {
        ByteBuffer buf = ByteBuffer.allocate(Constants.SAMPLES*2);
        InetSocketAddress ina = null;
        ina =(InetSocketAddress) ioSocket.receive(buf);
        if((ina != null) && (channels.get(selected-1).getState() != Constants.STATUS_RECORDING)) {
            short[] arr = buf.asShortBuffer().array();
            if(arr[0] == selected) {
                output.write(arr, 1, arr.length - 1);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        running = false;
    }

    public void run() {
        while(running) {
            if(recording) {
                send();
            }
            recieve();
            try {
                sleep(32);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<VoiceChannel> getChannels() {
        return channels;
    }

}