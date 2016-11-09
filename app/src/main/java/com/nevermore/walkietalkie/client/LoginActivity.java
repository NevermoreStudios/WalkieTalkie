package com.nevermore.walkietalkie.client;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nevermore.walkietalkie.R;
import com.nevermore.walkietalkie.server.ServerService;

public class LoginActivity extends Activity {

    private EditText username, serverip;

    public static final String EXTRA_USERNAME = "com.nevermore.walkietalkie.extra.username";
    public static final String EXTRA_SERVERIP = "com.nevermore.walkietalkie.extra.serverip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {
        username = (EditText)findViewById(R.id.login_username);
        serverip = (EditText) findViewById(R.id.login_serverip);
    }

    public void clickLogin(View view) {
        // This method is called when "Connect" button is clicked
        String usernameText = username.getText().toString(),
               serveripText = serverip.getText().toString();
        Intent data = new Intent(this, ChatService.class);
        data.putExtra(EXTRA_USERNAME, usernameText);
        data.putExtra(EXTRA_SERVERIP, serveripText);
        startService(data);
        startActivity(new Intent(this,MainActivity.class));
    }

    public void clickDiscover(View view) {
        startService(new Intent(this, DiscoveryService.class));
    }

    public void clickStartserver(View view) {
        startService(new Intent(this, ServerService.class));
    }

}