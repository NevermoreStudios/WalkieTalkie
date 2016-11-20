package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.R;
import com.nevermore.walkietalkie.server.ServerService;

import java.util.ArrayList;

public class ServerActivity extends BaseActivity {

    private EditText channelName;
    private ListView chatChannelList, voiceChannelList;
    private ArrayList<String> chatList = new ArrayList<>(), voiceList = new ArrayList<>();
    String username,serverip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        initUI();
        initDialog(R.string.server_help);
        refreshList(chatChannelList, chatList);
        refreshList(voiceChannelList, voiceList);
        username=getIntent().getStringExtra(Constants.EXTRA_USERNAME);
        serverip=getIntent().getStringExtra(Constants.EXTRA_SERVERIP);
    }

    private void initUI() {
        channelName = (EditText)findViewById(R.id.server_channel_input);
        chatChannelList = (ListView)findViewById(R.id.server_chat_channel_list);
        chatChannelList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        chatChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chatList.remove(position);
                refreshList(chatChannelList, chatList);
            }
        });
        voiceChannelList = (ListView)findViewById(R.id.server_voice_channel_list);
        voiceChannelList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        voiceChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                voiceList.remove(position);
                refreshList(voiceChannelList, voiceList);
            }
        });
    }

    private void refreshList(ListView listView, ArrayList<String> list) {
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.message_item, list));
    }

    private void onChannelAdd(ListView listView, ArrayList<String> list) {
        list.add(channelName.getText().toString());
        channelName.setText("");
        refreshList(listView, list);
    }

    public void onChatChannelAdd(View v) {
        if (!channelName.getText().toString().isEmpty()) {
            onChannelAdd(chatChannelList, chatList);
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.empty)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    public void onVoiceChannelAdd(View v) {
        if (!channelName.getText().toString().isEmpty()) {
            onChannelAdd(voiceChannelList, voiceList);
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.empty)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    public void onServerStart(View v) {
        if(!chatList.isEmpty()) {
            Intent i = new Intent(this, ServerService.class);
            i.putExtra(Constants.EXTRA_CHAT_CHANNELS, chatList);
            i.putExtra(Constants.EXTRA_VOICE_CHANNELS, voiceList);
            startService(i);
            Intent data = new Intent(this, ChatService.class);
            data.putExtra(Constants.EXTRA_USERNAME, username);
            data.putExtra(Constants.EXTRA_SERVERIP, serverip);
            startService(data);
        }else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_chat)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

}
