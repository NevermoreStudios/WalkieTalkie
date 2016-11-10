package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nevermore.walkietalkie.R;
import com.nevermore.walkietalkie.models.ChatMessage;

import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends Activity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter=0;

    private String username;
    private boolean bound;
    Button mic;
    Button send;
    View line;
    EditText input;
    ListView history;
    boolean micro=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mic= (Button) findViewById(R.id.main_voice);
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
        input = (EditText) findViewById(R.id.main_input);
        //adapter = new ArrayAdapter<>(this, R.layout.message_item, listItems);
        //history.setAdapter(adapter);
        //history.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //history.setOnClickListener(new View.OnClickListener(){
            //@Override
            //public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "u wot m8", Toast.LENGTH_LONG).show();
            //}
        //});
    }


    public void onClickSend(View view) {
        messageRecieved(new ChatMessage((byte)0, "", ""));
        // This is called when send button is clicked
    }

    private void onClickVoice() {
        // This is called when voice button is clicked
    }

    private void onReleaseVoice() {
        // This is called when voice button is clicked
    }

    public void messageRecieved(ChatMessage message) {
        listItems.add("Clicked : " + clickCounter++);
        adapter = new ArrayAdapter<>(this, R.layout.message_item, listItems);
        history.setAdapter(adapter);
    }

    private void hideMic() {
        Resources r = getResources();
        RelativeLayout.LayoutParams sendparams,micparams,lineparams,inputparams,historyparams;
        sendparams=(RelativeLayout.LayoutParams)send.getLayoutParams();
        micparams=(RelativeLayout.LayoutParams)mic.getLayoutParams();
        lineparams=(RelativeLayout.LayoutParams)line.getLayoutParams();
        inputparams=(RelativeLayout.LayoutParams)input.getLayoutParams();
        historyparams=(RelativeLayout.LayoutParams)history.getLayoutParams();
        sendparams.setMargins(0,0,0,0);
        micparams.height=0;
        lineparams.setMargins(0,0,0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        inputparams.setMargins(0,0,0,0);
        historyparams.setMargins(0,0,0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        send.setLayoutParams(sendparams);
        mic.setLayoutParams(micparams);
        line.setLayoutParams(lineparams);
        input.setLayoutParams(inputparams);
        history.setLayoutParams(historyparams);
    }

    private void showMic(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams sendparams,micparams,lineparams,inputparams,historyparams;
        sendparams=(RelativeLayout.LayoutParams)send.getLayoutParams();
        micparams=(RelativeLayout.LayoutParams)mic.getLayoutParams();
        lineparams=(RelativeLayout.LayoutParams)line.getLayoutParams();
        inputparams=(RelativeLayout.LayoutParams)input.getLayoutParams();
        historyparams=(RelativeLayout.LayoutParams)history.getLayoutParams();
        sendparams.setMargins(0,0,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm));
        micparams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        lineparams.setMargins(0,0,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, dm));
        inputparams.setMargins(0,0,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm));
        historyparams.setMargins(0,0,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, dm));
        send.setLayoutParams(sendparams);
        mic.setLayoutParams(micparams);
        line.setLayoutParams(lineparams);
        input.setLayoutParams(inputparams);
        history.setLayoutParams(historyparams);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        ChatService service;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChatService.ChatBinder binder = (ChatService.ChatBinder) service;
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }

    };



}
