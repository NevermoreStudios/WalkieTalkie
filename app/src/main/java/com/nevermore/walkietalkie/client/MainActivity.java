package com.nevermore.walkietalkie.client;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nevermore.walkietalkie.Constants;
import com.nevermore.walkietalkie.R;
import com.nevermore.walkietalkie.models.ChatChannel;
import com.nevermore.walkietalkie.models.VoiceChannel;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private ArrayList<String> listItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listMembers = new ArrayList<>();
    private ArrayList<String> listChat = new ArrayList<>();
    private ChatService service;
    private String username;
    private Button mic;
    private Button send;
    private View line;
    private EditText input;
    private ListView history;
    private ListView members;
    private ListView channels;
    private DrawerLayout drawers;
    private int chatId = 0;
    private int voiceId = -1;
    private ArrayList<ChatChannel> chatChannels;
    private ArrayList<VoiceChannel> voiceChannels;
    private int state;
    private ArrayList<ArrayList<String>> channelHistory= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username= getIntent().getStringExtra(Constants.EXTRA_USERNAME);
        bindService(new Intent(this, ChatService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        initUI();
        initDialog(R.string.main_help);
        setupIntentFilter();
        updateMic();
    }

    private void updateMic() {
        if(voiceId == -1) {
            hideMic();
        } else {
            showMic();
        }
    }

    private void initUI() {
        mic = (Button) findViewById(R.id.main_voice);
        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    onClickVoice();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onReleaseVoice();
                }
                return false;
            }
        });
        send = (Button) findViewById(R.id.main_send);
        line = findViewById(R.id.main_view);
        history = (ListView) findViewById(R.id.main_history_list);
        members = (ListView) findViewById(R.id.main_members);
        channels = (ListView) findViewById(R.id.main_chat_list);
        input = (EditText) findViewById(R.id.main_input);
        adapter = new ArrayAdapter<>(this, R.layout.message_item, listItems);
        ArrayAdapter<String> adapterMembers = new ArrayAdapter<>(this, R.layout.channel_item, listMembers);
        members.setAdapter(adapterMembers);
        history.setAdapter(adapter);
        history.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        members.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        channels.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        channels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                channelClicked(position);
            }
        });
        drawers = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        setChannelStatus(Constants.STATUS_AVAILABLE);
        listMembers.add(getString(R.string.no_members));
    }

    private void fillChannels() {
        chatChannels = service.ct.getChannels();
        voiceChannels = service.vt.getChannels();
        listChat.add(getString(R.string.chat_channels));
        for(ChatChannel c : chatChannels) {
            listChat.add("~ " + c.getName());
            channelHistory.add(new ArrayList<String>());
        }
        listChat.add(getString(R.string.voice_channels));
        for(VoiceChannel c : voiceChannels) {
            listChat.add("~ " + c.getName());
        }
        ArrayAdapter<String> adapterChat = new ArrayAdapter<>(this, R.layout.channel_item, listChat);
        channels.setAdapter(adapterChat);
        updateTitle();
    }

    private void setupIntentFilter() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(MainActivity.this, intent.getStringExtra(Constants.EXTRA_SENDER) + ": " + intent.getStringExtra(Constants.EXTRA_MESSAGE), Toast.LENGTH_LONG).show();
                if(username.equals(intent.getStringExtra(Constants.EXTRA_SENDER)))
                {
                    channelHistory.get((int)intent.getByteExtra(Constants.EXTRA_CHANNEL, (byte) 1)).add(getString(R.string.ME)+ ": " + intent.getStringExtra(Constants.EXTRA_MESSAGE));
                }
                else
                {
                    channelHistory.get((int)intent.getByteExtra(Constants.EXTRA_CHANNEL, (byte) 1)).add( intent.getStringExtra(Constants.EXTRA_SENDER)+ ": " + intent.getStringExtra(Constants.EXTRA_MESSAGE));
                }

                updateHistory();
            }
        }, new IntentFilter(Constants.RECEIVE_FILTER));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setChannelStatus(intent.getIntExtra(Constants.EXTRA_STATUS,Constants.STATUS_AVAILABLE));
            }
        }, new IntentFilter(Constants.STATUS_FILTER));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setMembers();
            }
        }, new IntentFilter(Constants.MEMBER_FILTER));
    }

    private void setMembers() {
        System.out.println("adaptiranje " + voiceId);
        adapter = new ArrayAdapter<>(MainActivity.this, R.layout.channel_item, voiceId == -1 ? listMembers : service.vt.getChannel().members);
        members.setAdapter(adapter);
    }

    private void updateTitle() {
        String title = getString(R.string.text) + ": " + chatChannels.get(chatId).getName();
        if(voiceId != -1) {
            title += " " + getString(R.string.voice) + ": " + voiceChannels.get(voiceId - 1).getName();
        }
        setTitle(title);
    }

    private void updateHistory() {
        adapter = new ArrayAdapter<>(MainActivity.this, R.layout.message_item,channelHistory.get(chatId));
        history.setAdapter(adapter);
    }

    private void channelClicked(int position) {
        if(position > 0 && position < chatChannels.size()+1) {
            chatId = position - 1;
            updateHistory();
            drawers.closeDrawers();
        } else if(position > chatChannels.size() + 1) {
            try{
                if(voiceId == position - chatChannels.size()-1) {
                    voiceId = -1;
                    service.vt.leaveChannel();
                } else {
                    if(voiceId == -1) {
                        voiceId = position - chatChannels.size() - 1;
                        service.vt.joinChannel((byte)(voiceId));
                    } else {
                        voiceId = position - chatChannels.size() - 1;
                        service.vt.changeChannel((byte)voiceId);
                    }
                }
                drawers.closeDrawers();
                updateMic();
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: Error handling
            }
        }
        updateTitle();
    }

    public void onClickSend(View view) {
        if(!input.getText().toString().isEmpty()){
        service.sendChatMsg((byte)chatId, input.getText().toString());
        input.setText("");
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

    private void onClickVoice() {
        if(state == Constants.STATUS_AVAILABLE) {
            service.vt.startRec();
        }
    }

    private void onReleaseVoice() {
        if(state == Constants.STATUS_RECORDING) {
            service.vt.stopRec();
        }
    }

    public void setChannelStatus(int state) {
        switch (state)
        {
            case Constants.STATUS_AVAILABLE :{mic.getBackground().setColorFilter(0xFF21EC0C, PorterDuff.Mode.MULTIPLY); break;}
            case Constants.STATUS_RECORDING :{mic.getBackground().setColorFilter(0xFF888888, PorterDuff.Mode.MULTIPLY); break;}
            case Constants.STATUS_SPEAKING :{mic.getBackground().setColorFilter(0xFFFFF700, PorterDuff.Mode.MULTIPLY); break;}
        }

        this.state = state;
    }

    private void hideMic() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams sendp = (RelativeLayout.LayoutParams)send.getLayoutParams(),
                micp = (RelativeLayout.LayoutParams)mic.getLayoutParams(),
                linep = (RelativeLayout.LayoutParams)line.getLayoutParams(),
                inputp = (RelativeLayout.LayoutParams)input.getLayoutParams(),
                historyp = (RelativeLayout.LayoutParams)history.getLayoutParams();
        sendp.setMargins(0, 0, 0, 0);
        micp.height = 0;
        linep.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm));
        inputp.setMargins(0, 0, 0, 0);
        historyp.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm));
        send.setLayoutParams(sendp);
        mic.setLayoutParams(micp);
        line.setLayoutParams(linep);
        input.setLayoutParams(inputp);
        history.setLayoutParams(historyp);
    }

    private void showMic(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams sendp = (RelativeLayout.LayoutParams)send.getLayoutParams(),
                micp = (RelativeLayout.LayoutParams)mic.getLayoutParams(),
                linep = (RelativeLayout.LayoutParams)line.getLayoutParams(),
                inputp = (RelativeLayout.LayoutParams)input.getLayoutParams(),
                historyp = (RelativeLayout.LayoutParams)history.getLayoutParams();
        sendp.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm));
        micp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        linep.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, dm));
        inputp.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm));
        historyp.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, dm));
        send.setLayoutParams(sendp);
        mic.setLayoutParams(micp);
        line.setLayoutParams(linep);
        input.setLayoutParams(inputp);
        history.setLayoutParams(historyp);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((ChatService.ChatBinder) binder).getService();
            fillChannels();
            updateHistory();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }

    };

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, ChatService.class));
        super.onDestroy();
    }

}
