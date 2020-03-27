package com.example.login.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.Fragment.FourthFragment;
import com.example.login.JSESSIONID;
import com.example.login.NetUtils;
import com.example.login.R;
import com.example.login.SaveCookie;
import com.example.login.websocket_Manager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class login extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.login.MESSAGE";

    private Intent intent;
    private EditText userID;
    private EditText password;

    private  String result;

    private String name,pwd;  //定义用户名和密码
    private int num=0;//用于存储登陆结果


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("login_activity","----onCreat()----");

        setContentView(R.layout.activity_login);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null)
        {
            actionbar.hide();
        }

        Intent intent =getIntent();
        String mes = intent.getStringExtra(FourthFragment.EXTRA_MESSAGE);
        if( mes!=null && mes.equals("退出登陆")){
            SaveCookie sc = new SaveCookie(getApplicationContext());
            sc.clear();
        }

        userID = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);


        Thread cookiejudge = new Thread() {
            @Override
            public void run() {
                num = cookie();
            }
        };
        cookiejudge.start();
        try {
            cookiejudge.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(num);
        if(num==200)
        {
            Intent it=new Intent(login.this, Main_Activity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(it);
        }
        else if(num==0)
        {
            Button registerbutton = (Button)findViewById(R.id.注册);
            registerbutton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Toast.makeText(login.this,"前往注册",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login.this , register.class);
                    String message = "请求注册";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }
            });

            Button findpasswordbutton = (Button)findViewById(R.id.忘记密码);
            findpasswordbutton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Toast.makeText(login.this,"前往找回密码",Toast.LENGTH_SHORT).show();
                }
            });

//            Button Test = (Button)findViewById(R.id.Test);
//            Test.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(login.this,"Test",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(login.this ,FriendsChat.class);
//                    startActivity(intent);
//                }
//            });

            Button loginbutton = (Button)findViewById(R.id.登陆);
            Log.e("login_Activity","loginbutton= "+loginbutton);
            loginbutton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    num=100;
                    EditText tvname=(EditText) findViewById(R.id.account); //获取用户控件
                    name= tvname.getText().toString(); //获取控件里面的值
                    EditText tvPsd=(EditText) findViewById(R.id.password);
                    pwd=tvPsd.getText().toString();

                    Thread loginjudge = new Thread() {
                        @Override
                        public void run() {
                            num= init(name,pwd);
                        }
                    };
                    if( !( name.equals("")|| pwd.equals((""))))
                    {
                        loginjudge.start();
                        try {
                            loginjudge.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText (getApplicationContext(),"用户名与密码不能为空！", Toast.LENGTH_LONG ).show();
                    }
//                new Thread()

                    if(num==0){   //判断id是否大于1，>1就是查询到有数据 ，可以进入主界面
                        Toast.makeText (getApplicationContext(),"登陆成功", Toast.LENGTH_LONG ).show();

                        websocket_Manager socketManager = new websocket_Manager();
                        socketManager.setUsername(name);        //把用户名传给websocket
                        socketManager.creat();

                        Intent it=new Intent(login.this, Main_Activity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(it);
                    }else if(num==1){
                        Toast.makeText (getApplicationContext(),"密码错误", Toast.LENGTH_LONG ).show();
                    }else if(num==2){
                        Toast.makeText (getApplicationContext(),"用户名未找到", Toast.LENGTH_LONG ).show();
                    }
                    else if(num==-1)
                    {
                        Toast.makeText (getApplicationContext(),"数据提交失败", Toast.LENGTH_LONG ).show();
                    }
                    loginjudge.interrupt();
                }

            });
        }
        cookiejudge.interrupt();
    }


    private int init(String name,String pwd){

//  192.168.3.138 这个ip地址是电脑Ipv4 地址 /20170112 是服务端的项目名称  /login/toJsonMain 是@RequestMapping的地址
        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/DoLogin";
        //String urlPath = "47.103.66.24:8080/TravelApp/account/DoLogin";

        URL url;
        int id = 10 ;
        String content = "uname="+name+"&pwd="+pwd;
        try {
            url=new URL(urlPath);
//            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("uname",name);  //参数put到json串里
//            jsonObject.put("pwd",pwd);
//            System.out.println(jsonObject.toString());

            //JSONObject Authorization =new JSONObject();
            //   Authorization.put("po类名",jsonObject 即po的字段)

//            String content=String.valueOf(jsonObject);  //json串转striect);  //ng类型

            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);

            conn.setDoOutput(true);

            conn.setDoInput(true);

            conn.setRequestMethod("POST");

            conn.setRequestProperty("ser-Agent", "Fiddler");

//            conn.setRequestProperty("Content-Type","application/json");
            //写输出流，将要转的参数写入流里

//            DataOutputStream os=new DataOutputStream(conn.getOutputStream());
            OutputStream os=conn.getOutputStream();

            os.write(content.getBytes()); //字符串写进二进流
            //os.writeBytes(content); //字符串写进二进流

            os.flush();
            os.close();


            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200

                //读取返回的json数据
                InputStream inputStream=conn.getInputStream();
                // 调用自己写的NetUtils() 将流转成string类型
                String json= NetUtils.readString(inputStream);

                System.out.println(json);

                Map<String,List<String>> cookies = conn.getHeaderFields();
                List<String> setCookies = cookies.get("Set-Cookie");

                String Cookie = null;
                                        for(String cookiecontent:setCookies)
                {
                    System.out.println(cookiecontent);
                    if(cookiecontent.contains("JSESS"))
                    {
                        JSESSIONID.setJSESSIONIDNAME(cookiecontent);
                        Log.d("Login","JSESSIONID被设置为："+cookiecontent);
                    }
                    else if(cookiecontent.contains("uname"))
                    {
                        Cookie = cookiecontent;
                    }
                }

                Log.e("JSESSID:",JSESSIONID.getJSESSIONIDNAME());

                SaveCookie sc = new SaveCookie(getApplicationContext());
                sc.saveinfile(Cookie);

                String test = sc.load();
                System.out.println("确认将cookie："+test+"写进文件");

                if(json.equals("登陆成功"))
                {
                    id=0;

                }
                else if(json.equals("密码错误"))
                {
                    id=1;
                }
                else if(json.equals("用户未找到"))
                {
                    id=2;
                }

            }else{
                id = -1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(id);
        return  id;
    }

    private int cookie(){

//  192.168.3.138 这个ip地址是电脑Ipv4 地址 /20170112 是服务端的项目名称  /login/toJsonMain 是@RequestMapping的地址
        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/judgeCookie";
        //    String urlPath="http://192.168.42.207:8080/20170112/login/toJsonMain.action"; 这个是实体机(手机)的端口
        URL url;
        int id = 0 ;
        try {
            url=new URL(urlPath);
//            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("uname",name);  //参数put到json串里
//            jsonObject.put("pwd",pwd);
//            System.out.println(jsonObject.toString());

            //JSONObject Authorization =new JSONObject();
            //   Authorization.put("po类名",jsonObject 即po的字段)

//            String content=String.valueOf(jsonObject);  //json串转string类型

            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);

            conn.setDoOutput(true);

            conn.setDoInput(true);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("ser-Agent", "Fiddler");

            SaveCookie sc = new SaveCookie(getApplicationContext());   //cookie的处理，调用save cookie的方法，对文件进行读和写
            String cookie = sc.load();
            System.out.println("读取cookie:"+cookie);
            if(cookie!=null) {
                conn.setRequestProperty("Cookie", cookie);
                System.out.println(cookie);
            }
            else
            {
                id = 0;
                return id;
            }

            InputStream inputStream = conn.getInputStream();
            // 调用自己写的NetUtils() 将流转成string类型

            String json= NetUtils.readString(inputStream);
            System.out.println(json+"json");

            String judge = conn.getHeaderField("ifLogin");

            websocket_Manager socketManager = new websocket_Manager();
            socketManager.setUsername(conn.getHeaderField("uname"));        //把用户名传给websocket
            socketManager.creat();

            Map<String,List<String>> cookies = conn.getHeaderFields();
            System.out.println("cookies:"+cookies);
            List<String> setCookies = cookies.get("Set-Cookie");
            System.out.println("setcookies:"+setCookies);

            String Cookie = null;
            for(String cookiecontent:setCookies)
            {
                System.out.println(cookiecontent);
                if(cookiecontent.contains("JSESS"))
                {
                    JSESSIONID.setJSESSIONIDNAME(cookiecontent);
                    Log.d("Login","JSESSIONID被设置为："+cookiecontent);
                }
            }

            id = Integer.parseInt(judge);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(id);
        return  id;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("login_activity","----onDestroy()----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("login_activity","----onResume()----");
        Intent intent =getIntent();
        String mes = intent.getStringExtra(FourthFragment.EXTRA_MESSAGE);
        if( mes!=null && mes.equals("退出登陆")){
            SaveCookie sc = new SaveCookie(getApplicationContext());
            sc.clear();
        }
        userID.setText("");
        password.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("login_activity","----onStart()----");
        Intent intent =getIntent();
        String mes = intent.getStringExtra(FourthFragment.EXTRA_MESSAGE);
        if( mes!=null && mes.equals("退出登陆")){
            SaveCookie sc = new SaveCookie(getApplicationContext());
            sc.clear();
        }
        userID.setText("");
        password.setText("");
    }
}

