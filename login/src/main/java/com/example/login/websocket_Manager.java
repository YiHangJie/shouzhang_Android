package com.example.login;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.login.Fragment.Fragment_confirmapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class websocket_Manager extends Service {

    private Context context;
    private static Thread websocket;
    public static String username;
    public static JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    String Message;

    //用于Activity和service通讯
    class JWebSocketClientBinder extends Binder {
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
//                                    Log.e("websocket_Manager","收到一条好友消息");
                                    Intent i = new Intent();
                                    i.setAction("com.example.login.servicecallback.content");
                                    i.putExtra("message", message);
//                                    Log.e("websocket_Manager", String.valueOf(i));
                                    ChatActivity.mContext.sendBroadcast(i);
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

        Log.d("websocket_Manager","--------creat()--------");
    }

    public static void said(String message)
    {
        Log.e("JWebSClientService", username+" send "+message);
        client.send(message);
    }

    public static void killwebsocket()
    {
        Log.e("JWebSClientService", username+" kill JWebSClientService");
        client.close();
    }


    public static String getUsername()
    {
        return username;
    }


    public static void setUsername(String uname)
    {
        username = uname;
    }
}
