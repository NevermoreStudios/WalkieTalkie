package com.nevermore.walkietalkie.server;

/**
 * Created by TEMP on 18-Oct-16.
 */
public class VoiceServer extends Thread{

    private boolean runing=true;

    public void Kill()
    {
        runing = false;
    }

    public void run()
    {
        while(runing)
        {

        }
    }
}
