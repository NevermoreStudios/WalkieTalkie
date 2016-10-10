package com.nevermore.walkietalkie;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.util.ArrayList;

/**
 * Created by TEMP on 10-Oct-16.
 */
public class VoiceThread extends  Thread{
    boolean Runing=true,Recording=false;
    AudioRecord Input;
    AudioTrack Output;

    public VoiceThread()
    {

    }

    public boolean init()
    {

        return  true;
    }
    public boolean Start()
    {
        Recording=true;
        return  true;
    }
    public boolean Stop()
    {
        Recording=false;
        return true;
    }

    public void Send()
    {


    }

    public void Recieve()
    {


    }

    public void KillThread()
    {
        Runing=false;
    }

    public void run()
    {
        while(Runing)
        {


        }

    }
}