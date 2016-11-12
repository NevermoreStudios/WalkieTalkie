package com.nevermore.walkietalkie.models;

import java.util.Date;

public class ChatMessage {

    private byte channelId;
    private String sender;
    public String message;
    Date timestamp;

    public ChatMessage(byte id, String sender, String msg) {
        this.channelId = id;
        this.message = msg;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public byte getChannel() {
        return channelId;
    }

}
