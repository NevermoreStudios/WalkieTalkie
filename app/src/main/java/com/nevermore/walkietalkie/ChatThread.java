package com.nevermore.walkietalkie;

public class ChatThread extends Thread{

    public ChatMessage recieve()
    {
        return new ChatMessage();
    }
    boolean runinng = true;

    public ChatThread()
    {
        //
    }

    public void send(ChatMessage msg)
    {
        //
    }

    public void kill()
    {
        runinng = false;
    }

    public void run()
    {
        while(runinng)
        {
            //
        }
    }
}
