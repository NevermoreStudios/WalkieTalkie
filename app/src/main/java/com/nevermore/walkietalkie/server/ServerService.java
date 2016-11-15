package com.nevermore.walkietalkie.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.Voice;
import android.util.Log;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.client.LoginActivity;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.VoiceChannel;

import org.json.JSONObject;

import java.util.ArrayList;

public class ServerService extends Service {
    ServerThread st;
    VoiceServer vs;
    ArrayList<ChatChannel> chatChannelsP = new ArrayList<>();
    ArrayList<VoiceChannel> voiceChannelsP = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initChannels(intent);
        st = new ServerThread(this, chatChannelsP, voiceChannelsP);
        st.start();
        System.out.println("wut");
        vs = new VoiceServer(this, voiceChannelsP);
        vs.start();
        System.out.println("wut2");
        return START_STICKY;
    }

    private void initChannels(Intent intent) {
        ArrayList<String> chatChannels = intent.getStringArrayListExtra(Constants.EXTRA_CHAT_CHANNELS),
                voiceChannels = intent.getStringArrayListExtra(Constants.EXTRA_VOICE_CHANNELS);
        for(int i = 0; i < chatChannels.size(); ++i) {
            chatChannelsP.add(new ChatChannel((byte)i, chatChannels.get(i)));
        }
        for(int i = 0; i < voiceChannels.size(); ++i) {
            voiceChannelsP.add(new VoiceChannel((byte)(i + Constants.CHANNEL_DELIMITER), chatChannels.get(i)));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        st.kill();
        vs.kill();
    }

}
