package com.nevermore.walkietalkie;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by TEMP on 10-Oct-16.
 */
public class Server {
    InetAddress Addres;
    int chat_port;
    int voice_port;
    ArrayList<ChatChannel> ChatChannels;
    ArrayList<VoiceChannel> VoiceChanels;
    int SelectedVoiceChanel;
    int SelectedChatChanel;
}
