package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nevermore.walkietalkie.R;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }

}
