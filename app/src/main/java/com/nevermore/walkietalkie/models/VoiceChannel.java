package com.nevermore.walkietalkie.models;

import com.nevermore.walkietalkie.models.Channel;

public class VoiceChannel extends Channel {

    private byte state = 0;
    private String speaker;

    public byte getState() {
        return state;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void set(int state, String speaker) {
        this.state = (byte)state;
        this.speaker = speaker;
    }

    public void setState(int state) {
        this.state = (byte)state;
    }

}
