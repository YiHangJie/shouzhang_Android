package com.example.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login.Adapter.MsgAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity  {

    public static Context mContext;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText edt_msg;
    private Button btn_send;
    private ArrayList<MsgEntity> list;//存放消息实体的集合
    private TextView toolbar_title;
    public MsgAdapter msgAdapter;

    public JWebSocketClient client;
    private websocket_Manager.JWebSocketClientBinder binder;
    private websocket_Manager jWebSClientService;

    private ChatMessageReceiver chatMessageReceiver;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务与活动成功绑定
            Log.e("ChatActivity", "服务与活动成功绑定");
            binder = (websocket_Manager.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //服务与活动断开
            Log.e("ChatActivity", "服务与活动成功断开");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = getApplicationContext();

        //绑定服务
        bindService();
        //注册广播
        doRegisterReceiver();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
        Intent intent = getIntent();

        //开启websocket
        websocket_Manager socketManager = new websocket_Manager();
        socketManager.setUsername(websocket_Manager.getUsername());        //把用户名传给websocket
        socketManager.creat();

        //初始化控件
        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar_chat);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        edt_msg=(EditText)findViewById(R.id.edt_msg);
        btn_send=(Button)findViewById(R.id.btn_send);
        toolbar_title = (TextView)findViewById(R.id.title_chat);

        //初始化消息
        SaveChatting sc = new SaveChatting(getApplicationContext());
        try {
            list = sc.load_chatdata(intent.getStringExtra("friend's name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(list==null)
        {
            list = new ArrayList<MsgEntity>();
        }

        msgAdapter=new MsgAdapter(this,list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(msgAdapter);

        toolbar_title.setText(intent.getStringExtra("friend's name"));

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send_content=edt_msg.getText().toString();
                if (!TextUtils.isEmpty(send_content)){
                    MsgEntity send_msg=new MsgEntity(MsgEntity.SEND_MSG,send_content);
                    list.add(send_msg);
                    //刷新RecyclerView显示
                    msgAdapter.notifyItemInserted(list.size()-1);
                    recyclerView.scrollToPosition(msgAdapter.getItemCount()-1);

                    Map<String , String> confirmation = new HashMap<String, String>();
                    confirmation.put("type","好友消息");
                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                    Date cur = new Date();
                    long temp = cur.getTime();
                    String now = simpleDateFormat.format(temp);
                    confirmation.put("time",now);

                    Map<String,String> text = new HashMap<String, String>();
                    text.put("from", websocket_Manager.getUsername());
                    text.put("to", toolbar_title.getText().toString());
                    text.put("content", edt_msg.getText().toString());

                    JSONObject message1 = new JSONObject(text);
                    Log.d("发送好友消息","message1.toString()"+message1.toString());
                    JSONObject message2 = new JSONObject(confirmation);
                    try {
                        message2.put("text",message1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    websocket_Manager.said(message2.toString());
//                    //模拟接受消息
//                    MsgEntity rcv_msg=new MsgEntity(MsgEntity.RCV_MSG,"我也感觉"+send_content);
//                    list.add(rcv_msg);
//                    msgAdapter.notifyItemInserted(list.size()-1);
//                    //将RecyclerView将显示的数据定位到最后一行
//                    recyclerView.scrollToPosition(list.size()-1);

                    edt_msg.setText("");//清空消息输入框
                }

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //绑定服务
        bindService();
        //注册广播
        doRegisterReceiver();
        //初始化消息
        SaveChatting sc = new SaveChatting(getApplicationContext());
        try {
            Intent intent = getIntent();
            list = sc.load_chatdata(intent.getStringExtra("friend's name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(list==null)
        {
            list = new ArrayList<MsgEntity>();
        }
        Log.d("ChatActivity_onRestart",list.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = getIntent();
        SaveChatting sc = new SaveChatting(getApplicationContext());
        try {
            sc.saveinfile(intent.getStringExtra("friend's name"),list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list.clear();
        //解除绑定服务，注销广播
        unRegistReceive();
        unbindService();
    }

    private class ChatMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.e("ChatActivity.ChatMessageReceiver",message);
            JSONObject mes = null;
            try {
                mes = new JSONObject(message);
                String content = mes.getJSONObject("text").getString("content");
                MsgEntity Msa_receive = new MsgEntity(MsgEntity.RCV_MSG,content);
                Msa_receive.setIsRead(MsgEntity.IS_READ);
                list.add(Msa_receive);
                //刷新RecyclerView显示
                msgAdapter.notifyItemInserted(list.size()-1);
                recyclerView.scrollToPosition(msgAdapter.getItemCount()-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 绑定服务
     */
    private void bindService() {
        Intent bindIntent = new Intent(ChatActivity.this, websocket_Manager.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }
    /**
     * 解绑服务
     */
    private void unbindService() {
        unbindService(serviceConnection);
    }
    /**
     * 动态注册广播
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.example.login.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
        Log.e("ChatActivity","动态注册广播成功");
    }
    /**
     * 动态注销广播
     */
    public void unRegistReceive() {
        unregisterReceiver(chatMessageReceiver);
        Log.e("ChatActivity","动态注销广播成功");
    }
}
