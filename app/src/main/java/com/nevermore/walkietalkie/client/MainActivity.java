package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.RelativeLayout;

import com.nevermore.walkietalkie.R;

import java.net.InetAddress;

public class MainActivity extends Activity {
    InetAddress serverAddress;
    VoiceThread vt;
    ChatThread ct;
    String username;
    DrawerLayout mDrawerLayout;
    RelativeLayout mLeftDrawer;
    RelativeLayout mRightDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        mRightDrawer=(RelativeLayout) findViewById(R.id.left_drawer);
        mLeftDrawer=(RelativeLayout) findViewById(R.id.right_drawer);
    }
}
