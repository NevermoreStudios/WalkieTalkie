package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.nevermore.walkietalkie.R;

public class BaseActivity extends AppCompatActivity {
    private AlertDialog dialog;

    protected void initDialog(int resId) {
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.help)
                .setMessage(resId)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_bar_help:
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
