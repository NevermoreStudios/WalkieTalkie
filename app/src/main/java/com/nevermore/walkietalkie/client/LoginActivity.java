package com.nevermore.walkietalkie.client;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.R;
import com.nevermore.walkietalkie.server.ServerService;

import java.io.IOException;
import java.net.InetAddress;

public class LoginActivity extends Activity {

    private EditText username, serverip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        try {
            //Constants.broadCast=getBroadcastAddress();
            Constants.broadCast=InetAddress.getByName("255.255.255.255");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    private void initUI() {
        username = (EditText)findViewById(R.id.login_username);
        serverip = (EditText) findViewById(R.id.login_serverip);
    }

    public void clickLogin(View view) {
        String usernameText = username.getText().toString(),
               serveripText = serverip.getText().toString();
        Intent data = new Intent(this, ChatService.class);
        data.putExtra(Constants.EXTRA_USERNAME, usernameText);
        data.putExtra(Constants.EXTRA_SERVERIP, serveripText);
        startService(data);
    }

    public void clickDiscover(View view) {
        String usernameText = username.getText().toString();
        new DiscoveryService(usernameText,this).execute(usernameText);

    }

    public void clickStartserver(View view) {
        Intent data = new Intent(this, ServerActivity.class);
        String usernameText = username.getText().toString();
        data.putExtra(Constants.EXTRA_USERNAME, usernameText);
        data.putExtra(Constants.EXTRA_SERVERIP, "127.0.0.1");
        startActivity(data);
    }

}