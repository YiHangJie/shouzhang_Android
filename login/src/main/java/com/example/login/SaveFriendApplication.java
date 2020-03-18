package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class SaveFriendApplication {

//    private List<String> friendApplication;
    private Context context;

    public SaveFriendApplication(Context context)
    {
        this.context = context;
    }

    public void saveinfile(JSONObject friendApplication)
    {
//        SharedPreferences shp = context.getSharedPreferences("friendsapplication",Context.MODE_PRIVATE);//安卓app内储存的键值对
//        SharedPreferences.Editor editor = shp.edit();
//        List<JSONObject> list_friendapplication = null;
//        Set<String> temp = null;
//        shp.getStringSet("friendsapplication",temp);
//
//        for(int i = 0;i<temp.size();i++)
//        {
//            JSONObject tempJson = temp.g
//            list_friendapplication.add(i,);
//        }
//        editor.putString("friendsapplication",friendApplication.toString());
//        editor.apply();
//        Log.e("SaveFriendApplication","写入手机文件成功！");
    }
}
