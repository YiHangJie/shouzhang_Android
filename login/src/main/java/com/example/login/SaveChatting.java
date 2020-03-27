package com.example.login;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.sf.json.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveChatting {
    private Context context;
    private MsgEntity msgEntity;

    public SaveChatting(Context context)
    {
        this.context = context;
    }


    public void saveinfile(String fname, ArrayList<MsgEntity> chatdata) throws JSONException {
        SharedPreferences shp = context.getSharedPreferences("chatdata",Context.MODE_PRIVATE);//安卓app内储存的键值对
        SharedPreferences.Editor editor = shp.edit();

        editor.remove(fname);
        editor.apply();

        JSONObject personal_chatdata = new JSONObject();
        JSONObject message_all = new JSONObject();
        for(int i = 0;i<chatdata.size();i++)
        {
            JSONObject message = new JSONObject();
            message.put("content",chatdata.get(i).getContent());
            message.put("type",chatdata.get(i).getType());
            message.put("isread",chatdata.get(i).getIsread());
            message_all.put(Integer.toString(i),message);
        }
        personal_chatdata.put("messageList",message_all);
        personal_chatdata.put("size",chatdata.size());
        editor.putString(fname,personal_chatdata.toString());
        editor.apply();
    }

    public ArrayList<MsgEntity> load_chatdata(String fname) throws JSONException {
        SharedPreferences shp = context.getSharedPreferences("chatdata",Context.MODE_PRIVATE);//安卓app内储存的键值对
        SharedPreferences.Editor editor = shp.edit();

        ArrayList<MsgEntity> chatdata = new ArrayList<MsgEntity>();

        JSONObject single = new JSONObject(shp.getString(fname,""));

        int size = single.getInt("size");

        JSONObject message_all = single.getJSONObject("messageList");

        for(int i = 0;i<size;i++)
        {
            JSONObject t = new JSONObject(message_all.get(Integer.toString(i)).toString());
            String content = t.getString("content");
            int type = t.getInt("type");
            int isread = t.getInt("isread");
            MsgEntity temp = new MsgEntity(type,content,isread);
            chatdata.add(temp);
        }

        //Log.e("SaveChatting",chatdata.get(0).getContent());
        return chatdata;
    }
}
