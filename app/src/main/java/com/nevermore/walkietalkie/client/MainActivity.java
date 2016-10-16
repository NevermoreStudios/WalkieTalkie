package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.os.Bundle;

import com.nevermore.walkietalkie.R;

import java.net.InetAddress;

public class MainActivity extends Activity {
    InetAddress serverAddress;
    VoiceThread vt;
    ChatThread ct;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
