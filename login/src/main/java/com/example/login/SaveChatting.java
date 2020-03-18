package com.example.login;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
        for(int i = 0;i<chatdata.size();i++)
        {
            personal_chatdata.putOpt(Integer.toString(i),chatdata.get(i));
        }
        personal_chatdata.put("size",chatdata.size());
        editor.putString(fname,personal_chatdata.toString());
        editor.apply();
    }

    public ArrayList<MsgEntity> load_chatdata(String fname) throws JSONException {
        SharedPreferences shp = context.getSharedPreferences("chatdata",Context.MODE_PRIVATE);//安卓app内储存的键值对
        SharedPreferences.Editor editor = shp.edit();

        ArrayList<MsgEntity> chatdata = new ArrayList<MsgEntity>();

        net.sf.json.JSONObject temp = new net.sf.json.JSONObject();

        JSONObject personal_data = new JSONObject(shp.getString(fname,null));

        int size = personal_data.getInt("size");

        for(int i = size-1;i>=size-100;i--)
        {
            chatdata.add(0, net.sf.json.JSONObject.toBean(personal_data.getJSONObject(Integer.toString(i)),MsgEntity.class));
        }
        return chatdata;
    }
}
