package com.example.login;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.login.Activity.ChatActivity;
import com.example.login.Fragment.Fragment_confirmapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class websocket_Manager extends Service {

    private Context context;
    private static Thread websocket;
    public static String username;
    public static JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    String Message;

    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public websocket_Manager getService() {
            return websocket_Manager.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void creat()
    {
        websocket = new Thread(){
            @Override
            public void run() {
                URI uri = URI.create("ws://www.lovecurry.club:8080/TravelApp/websocket/"+username);
                client = new JWebSocketClient(uri) {
                    @Override
                    public void onMessage(String message) {
                        //message就是接收到的消息
                        Log.e("JWebSClientService", message);
                        try {
                            JSONObject mes = new JSONObject(message);
                            String what = mes.getString("type");
                            String name = mes.getJSONObject("text").getString("from");
                            switch (what){
                                case "好友请求":
                                    Fragment_confirmapplication.nameList.add(name);
                                    break;
                                case "好友消息":
//                                    Log.d("websocket_Manager","收到一条好友消息");
                                    Intent i1 = new Intent();
                                    i1.setAction("com.example.login.servicecallback.content");
                                    i1.putExtra("type","text");
                                    i1.putExtra("message", message);
//                                    Log.e("websocket_Manager", String.valueOf(i));
                                    ChatActivity.mContext.sendBroadcast(i1);
                                    Log.e("websocket_Manager","已发送广播");
                                    break;
                                case "好友图片消息":
                                    Intent i2 = new Intent();
                                    i2.setAction("com.example.login.servicecallback.content");
                                    i2.putExtra("type","img");
                                    i2.putExtra("message", message);
//                                    Log.e("websocket_Manager", String.valueOf(i));
                                    ChatActivity.mContext.sendBroadcast(i2);
                                    Log.e("websocket_Manager","已发送广播");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                client.connect();
            }
        };
        websocket.start();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
        Log.d("websocket_Manager","--------creat()--------");
    }

    public static void said(String message)
    {
        Log.d("JWebSClientService", username+" send "+message);
        client.send(message);
    }

    public static void killwebsocket()
    {
        Log.e("JWebSClientService", username+" kill JWebSClientService");
        client.close();
        client = null;
    }


    public static String getUsername()
    {
        return username;
    }


    public static void setUsername(String uname)
    {
        username = uname;
    }

    //    -----------------------------------消息通知--------------------------------------------------------

    /**
     * 检查锁屏状态，如果锁屏先点亮屏幕
     *
     * @param content
     */
    private void checkLockAndShowNotification(String content) {
        //管理锁屏的一个服务
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {//锁屏
            //获取电源管理器对象
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();  //点亮屏幕
                wl.release();  //任务结束后释放
            }
            sendNotification(content);
        } else {
            sendNotification(content);
        }
    }

    /**
     * 发送通知
     *
     * @param content
     */
    private void sendNotification(String content) {
        Intent intent = new Intent();
        intent.setClass(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                // 设置该通知优先级
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle("服务器")
                .setContentText(content)
                .setVisibility(VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                // 向通知添加声音、闪灯和振动效果
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();
        notifyManager.notify(1, notification);//id要保证唯一
    }

    //    -------------------------------------websocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("websocket_Manager", "心跳包检测websocket连接状态");
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                creat();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e("JWebSocketClientService", "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
