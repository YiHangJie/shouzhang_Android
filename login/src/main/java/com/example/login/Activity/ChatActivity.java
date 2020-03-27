package com.example.login.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login.Adapter.MsgAdapter;
import com.example.login.JWebSocketClient;
import com.example.login.MsgEntity;
import com.example.login.NetUtils;
import com.example.login.R;
import com.example.login.SaveChatting;
import com.example.login.websocket_Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        //检测通知是否开启
        checkNotification(mContext);

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

        // 初始化消息
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

    /**
     * 检测是否开启通知
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
    /**
     * 如果没有开启通知，跳转至设置界面
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * 获取通知权限,监测是否开启了系统通知
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
