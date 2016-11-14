package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.server.ServerService;

import java.util.ArrayList;

public class ServerActivity extends Activity {

    private EditText channelName;
    private ListView channelList;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        initUI();
        refreshList();
    }

    private void initUI() {
        channelName = (EditText)findViewById(R.id.server_channel_input);
        channelList = (ListView)findViewById(R.id.server_channel_list);
        channelList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.remove(position);
                refreshList();
            }
        });
    }

    private void refreshList() {
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_item, list);
        channelList.setAdapter(listAdapter);
    }

    public void onChannelAdd(View v) {
        list.add(channelName.getText().toString());
        refreshList();
    }

    public void onServerStart(View v) {
        Intent i = new Intent(this, ServerService.class);
        i.putExtra(Constants.EXTRA_CHANNELS, list);
        startService(i);
    }

}
