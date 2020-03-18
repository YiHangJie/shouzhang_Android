package com.example.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class register extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.register.MESSAGE";
    private Button loginbutton,registerbutton,forgetpasswordbutton;


    private Intent intent;
//    private EditText account = findViewById(R.id.account);
//    private EditText password = findViewById(R.id.password);
//    private EditText passwordconfirm = findViewById(R.id.passwordconfirm);

    private  String result;

    private String name,Email,pwd1,pwd2;  //定义用户名和密码
    private int num=0;//用于存储与服务器沟通的结果
    private String vericationcode="";   //验证码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null)
        {
            actionbar.hide();
        }

        final EditText editText1 = (EditText) findViewById(R.id.account);
        Drawable forma1 = getResources().getDrawable(R.mipmap.forma1);
        forma1.setBounds(0, 0, 130, 130 );//第一0是距左边距离，第二0是距上边距离，125分别是长宽
        editText1.setCompoundDrawables(forma1, null, null, null);//只放左边

        EditText editText2 = (EditText) findViewById(R.id.Email);
        Drawable forma2 = getResources().getDrawable(R.mipmap.forma2);
        forma2.setBounds(0, 0, 130, 130 );//第一0是距左边距离，第二0是距上边距离，125分别是长宽
        editText2.setCompoundDrawables(forma2, null, null, null);//只放左边

        EditText editText3 = (EditText) findViewById(R.id.password);
        Drawable forma3 = getResources().getDrawable(R.mipmap.forma3);
        forma3.setBounds(0, 0, 130, 130 );//第一0是距左边距离，第二0是距上边距离，125分别是长宽
        editText3.setCompoundDrawables(forma3, null, null, null);//只放左边

        final EditText editText4 = (EditText) findViewById(R.id.passwordconfirm);
        forma3 = getResources().getDrawable(R.mipmap.forma3);
        forma3.setBounds(0, 0, 130, 130 );//第一0是距左边距离，第二0是距上边距离，125分别是长宽
        editText4.setCompoundDrawables(forma3, null, null, null);//只放左边

        final EditText editText5 = (EditText) findViewById(R.id.verication_code);
        Drawable A = getResources().getDrawable(R.mipmap.a);
        A.setBounds(0, 0, 130, 130 );//第一0是距左边距离，第二0是距上边距离，125分别是长宽
        editText5.setCompoundDrawables(A, null, null, null);//只放左边

        final CountdownButton getvericationcode = (CountdownButton)findViewById(R.id.getvericationcode);
        getvericationcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                EditText account=(EditText) findViewById(R.id.account); //获取用户控件
                name= account.getText().toString(); //获取控件里面的值
                EditText email=(EditText) findViewById(R.id.Email); //获取用户控件
                Email= email.getText().toString(); //获取控件里面的值
                EditText password=(EditText) findViewById(R.id.password);
                pwd1=password.getText().toString();
                EditText passwordconfirm=(EditText) findViewById(R.id.passwordconfirm);
                pwd2=passwordconfirm.getText().toString();

                if(Email.contains("@")&&Email.contains(".")) {
                    Thread vericationcode = new Thread() {
                        @Override
                        public void run() {
                            num = DoEmail(Email);
                        }
                    };
                    vericationcode.start();
                    try {
                        vericationcode.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (num == -1) {   //判断id是否为-1，-1就是与服务器连接出现问题
                        Toast.makeText(getApplicationContext(), "提交数据失败", Toast.LENGTH_LONG).show();
                    } else if (num == 1) {
                        Toast.makeText(getApplicationContext(), "请去邮箱查收验证码！", Toast.LENGTH_LONG).show();
                    }else if(num==0){
                        Toast.makeText(getApplicationContext(), "该邮箱已被注册！", Toast.LENGTH_LONG).show();
                        email.setText("");
                    }
                }
                else {
                    Toast.makeText (getApplicationContext(),"邮箱格式错误，请重新输入！", Toast.LENGTH_LONG ).show();
                    email.setText("");
                }
            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Email = s.toString().trim();
                if(Email.contains("@")&&Email.contains("."))
                {
                    getvericationcode.setEnabled(true);
                }
                else
                {
                    getvericationcode.setEnabled(false);
                }
            }
        });

        Button OK = (Button)findViewById(R.id.OK);
        OK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                EditText account=(EditText) findViewById(R.id.account); //获取用户控件
                name= account.getText().toString(); //获取控件里面的值
                EditText verication=(EditText) findViewById(R.id.verication_code);
                EditText password=(EditText) findViewById(R.id.password); //获取用户控件
                pwd1= password.getText().toString(); //获取控件里面的值
                EditText passwordconfirm=(EditText) findViewById(R.id.passwordconfirm); //获取用户控件
                pwd2= passwordconfirm.getText().toString(); //获取控件里面的值
                EditText email=(EditText) findViewById(R.id.Email); //获取用户控件
                String tem= email.getText().toString(); //获取控件里面的值
                String temp = verication.getText().toString();

                if(name.equals(""))
                {
                    Toast.makeText (getApplicationContext(),"用户名为空！", Toast.LENGTH_LONG ).show();
                    return;
                }
                else if(!pwd1.equals(pwd2))
                {
                    Toast.makeText (getApplicationContext(),"密码不一致，请重新输入！", Toast.LENGTH_LONG ).show();
                    password.setText("");
                    passwordconfirm.setText("");
                    return;
                }else if(!Email.equals(tem))
                {
                    Toast.makeText (getApplicationContext(),"请输入正确的邮箱名！", Toast.LENGTH_LONG ).show();
                    email.setText("");
                    return;
                }

                if(vericationcode.equals(temp)&&(!vericationcode.equals("")))
                {
                    Thread OK = new Thread() {
                        @Override
                        public void run() {
                            num= DoOK(Email,name,pwd1);
                        }
                    };
                    OK.start();
                    try {
                        OK.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                new Thread()

                    if(num==-1){   //判断id是否为-1，-1就是与服务器连接出现问题
                        Toast.makeText (getApplicationContext(),"提交数据失败", Toast.LENGTH_LONG ).show();
                        return;
                    }
                    else if(num==0){
                        Toast.makeText(register.this, "用户名已被使用!", Toast.LENGTH_SHORT).show();
                        account.setText("");
                        verication.setText("");
                        return;
                    }
                    else if(num==1)
                    {
                        Toast.makeText(register.this, "注册成功!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                else if(vericationcode.equals(""))
                {
                    Toast.makeText(register.this,"请先获取验证码！",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(register.this,"注册失败，验证码错误！",Toast.LENGTH_SHORT).show();
                    verication.setText("");
                    return;
                }
            }
        });

        Button  cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(register.this,"返回登陆界面",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(register.this, login.class);
                String message = "返回登陆界面";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                finish();
            }
        });

    }


    private int DoEmail(String Email){                           //与后台建立联系，成功则返回验证码，失败则返回-1

//  192.168.3.138 这个ip地址是电脑Ipv4 地址 /20170112 是服务端的项目名称  /login/toJsonMain 是@RequestMapping的地址
        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/DoEmail";
        //    String urlPath="http://192.168.42.207:8080/20170112/login/toJsonMain.action"; 这个是实体机(手机)的端口
        URL url;
        int id=0;
        String content = "anEmail="+Email;
        try {
            url=new URL(urlPath);

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

            vericationcode = "";
            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200
                //读取返回的json数据
                InputStream inputStream=conn.getInputStream();
                // 调用自己写的NetUtils() 将流转成string类型
                String json= NetUtils.readString(inputStream);
                System.out.println(json);
                if(json.equals("已注册"))
                {
                    id = 0;
                }
                else
                {
                    vericationcode = json;
                    id=1;
                }
            }else{
                vericationcode = "";
                id = -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(vericationcode);
        return  id;
    }

    private int DoOK(String Email,String name,String password){                           //与后台建立联系，成功则返回验证码，失败则返回-1

//  192.168.3.138 这个ip地址是电脑Ipv4 地址 /20170112 是服务端的项目名称  /login/toJsonMain 是@RequestMapping的地址
        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/DoRegister";
        //    String urlPath="http://192.168.42.207:8080/20170112/login/toJsonMain.action"; 这个是实体机(手机)的端口
        URL url;

        int id=0;
        String content = "anEmail="+Email+"&uname="+name+"&pwd="+password;
        try {
            url=new URL(urlPath);

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
                if(json.equals("注册成功"))
                {
                    id = 1;
                }
                else if(json.equals("用户名已使用"))
                {
                    id=0;
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
}

