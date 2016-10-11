package com.nevermore.walkietalkie;

import android.media.AudioRecord;
import android.media.AudioTrack;

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

        return  true;
    }
    public boolean startRec()
    {
        recording=true;
        return true;
    }
    public boolean stopRec()
    {
        recording=false;
        return true;
    }

    public void send()
    {


    }

    public void recieve()
    {


    }

    public void kill()
    {
        running=false;
    }

    public void run()
    {
        while(running)
        {


        }

    }
}