package com.nevermore.walkietalkie;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by TEMP on 10-Oct-16.
 */
public class Server {
    InetAddress addres;
    int chatport;
    int voiceport;
    ArrayList<ChatChannel> chatChannels;
    ArrayList<VoiceChannel> voiceChanels;
    int selectedVoiceChanel;
    int selectedChatChanel;
}
