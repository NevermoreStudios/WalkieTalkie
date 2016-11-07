package com.nevermore.walkietalkie.client;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nevermore.walkietalkie.R;

public class LoginActivity extends Activity {

    private EditText username, serverip;

    public static final String EXTRA_USERNAME = "Vrabac je instanca";
    public static final String EXTRA_SERVERIP = "Svemoguci Stojan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "lol", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, new IntentFilter("lolololo"));
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
        startService(new Intent(this, ChatService.class));
    }

    public void clickDiscover(View view) {
        // This method is called when "Discover" button is clicked
    }

    public void clickStartserver(View view) {
        // This method is called when "Start server" button is clicked
    }

}
