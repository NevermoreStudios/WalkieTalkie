package com.nevermore.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.rtp.AudioStream;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VoiceThread extends  Thread{
    boolean running=true,recording=false;
    AudioRecord input;
    AudioTrack output;
    MainActivity parent;

    public VoiceThread(MainActivity ma)
    {
        parent =ma;
    }

    public boolean init()
    {
        input = new AudioRecord(MediaRecorder.AudioSource.MIC,44100, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,44100);
        output = new AudioTrack(AudioManager.STREAM_VOICE_CALL,44100,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,44100,AudioTrack.MODE_STREAM);

        return  true;
    }
    public boolean startRec()
    {
        recording=true;
        input.startRecording();
        return true;
    }
    public boolean stopRec()
    {
        recording=false;
        input.stop();
        return true;
    }

    public void send(short val)
    {


    }

    public short recieve()
    {

            return 1;
    }

    public void kill()
    {
        running=false;
    }

    public void run()
    {
        short buff[]= new short[1];
        short in[]= new short[1];
        while(running)
        {
            if(recording)
            {
                output.play();
                input.read(buff,0,1);
                send(buff[0]);
            }
            else
            {
                output.play();
                //while(1 > 0 )
                //{
                    in[0]=recieve();
                    output.write(in,0,1);
                //}
            }

        }

    }
}