package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SaveCookie {

    private Context context;

    public SaveCookie(Context context)
    {
        this.context = context;
    }

    public void saveinfile(String cookieContent)
    {
        SharedPreferences shp = context.getSharedPreferences("cookie",Context.MODE_PRIVATE);//安卓app内储存的键值对
        SharedPreferences.Editor editor = shp.edit();
        editor.remove("cookie");
        editor.remove("deadline");
        editor.apply();

        //该函数返回以毫秒为单位的当前时间
        long onehourlaterTime = System.currentTimeMillis() + 60 * 60 * 1000;
        Date ddl = new Date(onehourlaterTime);                      //cookie的死亡时间
        String nowtime = ddl.toString();            //nowtime是cookie死亡时间的字符串形式

        editor.putString("deadline",nowtime);
        editor.putString("cookie",cookieContent);
        editor.apply();

    }

    public String load()
    {
        String cookiecontent;
        SharedPreferences shp = context.getSharedPreferences("cookie",Context.MODE_PRIVATE);//安卓app内储存的键值对
        SharedPreferences.Editor editor = shp.edit();

        String ddl = shp.getString("deadline",null);
        cookiecontent = shp.getString("cookie",null);

        Date nowtime = new Date();
        Date deadline = new Date(ddl);

        boolean if_cookiealive = nowtime.before(deadline);

        if(if_cookiealive)
        {
            System.out.println(nowtime + "  " + cookiecontent);
            editor.apply();
            return cookiecontent;
        }
        else
            return null;
    }

    public void clear()
    {
        SharedPreferences shp = context.getSharedPreferences("cookie",Context.MODE_PRIVATE);//安卓app内储存的键值对
        SharedPreferences.Editor editor = shp.edit();
        editor.clear();
        editor.apply();
    }
}
