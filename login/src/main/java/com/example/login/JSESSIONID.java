package com.example.login;

import android.util.Log;

public class JSESSIONID {
    static private String JSESSIONIDNAME = "";

    static{
        Log.e("JSESSION","JSESSION 初始化块");
    }

    public static void setJSESSIONIDNAME(String JESS){
        JSESSIONID.JSESSIONIDNAME = JESS;
    }
    public static String getJSESSIONIDNAME(){
        return JSESSIONIDNAME;
    }
}
