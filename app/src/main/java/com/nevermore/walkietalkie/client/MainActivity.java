package com.nevermore.walkietalkie.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nevermore.walkietalkie.models.ChatMessage;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ArrayList<String> listItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int clickCounter = 0;
    private ChatService service;
    private String username;
    private boolean bound;
    private Button mic;
    private Button send;
    private View line;
    private EditText input;
    private ListView history;
    private boolean micro = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        input = (EditText) findViewById(R.id.main_input);
        adapter = new ArrayAdapter<String>(this, R.layout.message_item, listItems);
            history.setAdapter(adapter);
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
        adapter = new ArrayAdapter<String>(this, R.layout.message_item, listItems);
        history.setAdapter(adapter);
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
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            service = null;
        }

    };

}
