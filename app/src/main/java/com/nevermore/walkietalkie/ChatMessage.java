package com.nevermore.walkietalkie;

import java.util.Date;

public class ChatMessage {
    public ChatMessage(byte id,String msg,String sender)
    {
        this.channelId=id;
        this.message = msg;
        this.sender = sender;
    }

    byte channelId;
    String sender;
    String message;
    Date timestamp;
}
