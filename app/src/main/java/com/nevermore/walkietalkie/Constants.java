package com.nevermore.walkietalkie;

import java.net.InetAddress;

public class Constants {

    public static final int CHAT_PORT = 53729;
    public static final int VOICE_PORT = 53730;
    public static final int CHAT_SERVER_PORT = 53731;
    public static final int VOICE_SERVER_PORT = 9999;
    public static final int DISCOVERY_PORT = 9991;
    public static final byte CHANNEL_DELIMITER = (byte)64;
    public static final String DELIMITER = "" + ((char)65535);
    public static final String RECEIVE_FILTER = "com.nevermore.walkietalkie.filter.receive";
    public static final String TOAST_FILTER = "com.nevermore.walkietalkie.filter.toast";
    public static final String STATUS_FILTER = "com.nevermore.walkietalke.filter.status";
    public static final String EXTRA_USERNAME = "com.nevermore.walkietalke.extra.username";
    public static final String EXTRA_SERVERIP = "com.nevermore.walkietalke.extra.serverip";
    public static final String EXTRA_CHAT_CHANNELS = "com.nevermore.walkietalke.extra.cchannels";
    public static final String EXTRA_VOICE_CHANNELS = "com.nevermore.walkietalke.extra.vchannels";
    public static final String EXTRA_MESSAGE = "com.nevermore.walkietalke.extra.message";
    public static final String EXTRA_SENDER = "com.nevermore.walkietalke.extra.sender";
    public static final String EXTRA_CHANNEL = "com.nevermore.walkietalke.extra.channel";
    public static final String EXTRA_STATUS = "com.nevermore.walkietalke.extra.status";
    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SPEAKING = 1;
    public static final int STATUS_RECORDING = 2;
    public static InetAddress broadCast;

}
