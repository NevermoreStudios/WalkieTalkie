package com.nevermore.walkietalkie;

import android.content.IntentSender;

/**
 * Created by TEMP on 10-Oct-16.
 */
public class ChatThread extends Thread{

    public ChatMessage Rcieve()
    {
        return new ChatMessage();
    }
    boolean Runing=true;

    public ChatThread()
    {

    }

    public void Send(ChatMessage msg)
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
