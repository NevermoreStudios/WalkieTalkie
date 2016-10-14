package com.nevermore.walkietalkie;

import android.app.Activity;
import android.os.Bundle;

import java.net.InetAddress;

public class MainActivity extends Activity {
    InetAddress serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
