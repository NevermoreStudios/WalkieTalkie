package com.nevermore.walkietalkie.models;

import java.util.ArrayList;
import java.util.List;


public abstract class Channel {

    protected String name;
    protected byte id;

    public Channel(byte id, String name) {
        this.id = id;
        this.name = name;
    }

}
