package com.example.login.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.Adapter.MsgAdapter;
import com.example.login.JSESSIONID;
import com.example.login.JWebSocketClient;
import com.example.login.MsgEntity;
import com.example.login.R;
import com.example.login.SaveChatting;
import com.example.login.websocket_Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;

    public static Context mContext;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText edt_msg;
    private Button btn_send;
    private Button btn_album;
    private Button btn_camera;
    private ImageButton btn_moreaction;
    private GridLayout layout_grid;
    private ArrayList<MsgEntity> list;//存放消息实体的集合
    private TextView toolbar_title;
    public MsgAdapter msgAdapter;

    private Bitmap fhead;
    private Bitmap uhead;

    private String tempTime;
    private String tempUri;
    private int flag = 0;

    private String fname;
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
        fname = intent.getStringExtra("friend's name");

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
        btn_album = (Button)findViewById(R.id.chat_album);
        btn_camera = (Button)findViewById(R.id.chat_camera);
        btn_moreaction = (ImageButton)findViewById(R.id.chat_moreAction);
        layout_grid = (GridLayout)findViewById(R.id.chat_grid);

        // 初始化消息
        SaveChatting sc = new SaveChatting(getApplicationContext());
        try {
            list = sc.load_chatdata(fname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(list==null)
        {
            list = new ArrayList<MsgEntity>();
        }

        msgAdapter=new MsgAdapter(this,list,fname);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(msgAdapter);
        recyclerView.setItemAnimator(null);

        toolbar_title.setText(intent.getStringExtra("friend's name"));

        btn_send.setOnClickListener(this);

        btn_album.setOnClickListener(this);

        btn_camera.setOnClickListener(this);

        btn_moreaction.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置moreaction不可见
        layout_grid.setVisibility(View.GONE);
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
            list = sc.load_chatdata(fname);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = getIntent();
        SaveChatting sc = new SaveChatting(getApplicationContext());
        try {
            sc.saveinfile(fname,list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list.clear();
        //解除绑定服务，注销广播
        unRegistReceive();
        unbindService();

        Thread GetFriendsName = new Thread() {
            @Override
            public void run() {
                File target = DownloadHeadPicFromServer(fname);
            }
        };
        GetFriendsName.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
            {
                String send_content = edt_msg.getText().toString();
                if (!TextUtils.isEmpty(send_content)) {
                    MsgEntity send_msg = new MsgEntity(MsgEntity.SEND_MSG, send_content, MsgEntity.IS_READ);
                    list.add(send_msg);
                    //刷新RecyclerView显示
                    msgAdapter.notifyItemInserted(list.size() - 1);
                    recyclerView.scrollToPosition(msgAdapter.getItemCount() - 1);

                    Map<String, String> confirmation = new HashMap<String, String>();
                    confirmation.put("type", "好友消息");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                    Date cur = new Date();
                    long temp = cur.getTime();
                    String now = simpleDateFormat.format(temp);
                    confirmation.put("time", now);

                    Map<String, String> text = new HashMap<String, String>();
                    text.put("from", websocket_Manager.getUsername());
                    text.put("to", toolbar_title.getText().toString());
                    text.put("content", edt_msg.getText().toString());

                    JSONObject message1 = new JSONObject(text);
                    Log.d("发送好友消息", "message1.toString()" + message1.toString());
                    JSONObject message2 = new JSONObject(confirmation);
                    try {
                        message2.put("text", message1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    websocket_Manager.said(message2.toString());
//                    //模拟接受消息
////                    MsgEntity rcv_msg=new MsgEntity(MsgEntity.RCV_MSG,"我也感觉"+send_content);
////                    list.add(rcv_msg);
//                    msgAdapter.notifyItemInserted(list.size()-1);
//                    //将RecyclerView将显示的数据定位到最后一行
//                    recyclerView.scrollToPosition(list.size()-1);

                    edt_msg.setText("");//清空消息输入框
                }
                break;
            }
            case R.id.chat_moreAction:
            {
                layout_grid.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.chat_album:
            {
                flag = 0;
                //"点击了相册";
                //  6.0之后动态申请权限 SD卡写入权限
                if (ContextCompat.checkSelfPermission(ChatActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE2);

                } else {
                    //打开相册
                    choosePhoto();
                }
                edt_msg.setText("");//清空消息输入框
                break;
            }
        }
    }

    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 2);
    }

    private void takePhoto() throws IOException {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 获取文件
        File file = createFileIfNeed("UserIcon.png");
        //拍照后原图回存入此路径下
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uri = FileProvider.getUriForFile(this, "com.example.login.UpdateHeadPic_Activity", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1);
    }

    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
    private File createFileIfNeed(String fileName) throws IOException {
        String fileA = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.example.login/files/chatImg/";
        File fileJA = new File(fileA);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(fileA, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 申请权限回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_ADD_CASE_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,"拒绝了你的请求",Toast.LENGTH_SHORT).show();
                //"权限拒绝");

            }
        }


        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                //"权限拒绝");

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * startActivityForResult执行后的回调方法，接收返回的图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd hh_mm_ss");
        Date cur = new Date();
        long temp = cur.getTime();
        final String now = simpleDateFormat.format(temp);
        final String from = websocket_Manager.getUsername();
        final String to = toolbar_title.getText().toString();
        tempTime = now;
        tempUri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/chatImg/" + now+"_"+from+"_" +to+ ".png";

//        try {
//            createFileIfNeed(now+"_"+from+"_" +to+ ".png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (requestCode == 1 && resultCode != Activity.RESULT_CANCELED) {

            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) return;
            try {
                Uri selectedImage = data.getData();//获取路径
                Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                saveImageToLocal(bit,tempUri,now,from,to);
            } catch (Exception e) {
                //"上传失败");
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();//获取路径
                Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                saveImageToLocal(bit,tempUri,now,from,to);

                Map<String, String> confirmation = new HashMap<String, String>();
                confirmation.put("type", "好友图片消息");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                Date curor = new Date();
                long tempint = curor.getTime();
                String now_str = simpleDateFormat.format(tempint);
                confirmation.put("time", now_str);

                Map<String, String> text = new HashMap<String, String>();
                text.put("from", from);
                text.put("to", to);

                String content = now+"_"+from+"_" +to+ ".png";
                text.put("content", content);

                JSONObject message1 = new JSONObject(text);

                JSONObject message2 = new JSONObject(confirmation);
                try {
                    message2.put("text", message1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                websocket_Manager.said(message2.toString());

                MsgEntity send_msg = new MsgEntity(MsgEntity.SEND_MSG, "" ,MsgEntity.UN_READ,tempUri,MsgEntity.IS_Img,tempTime);
                list.add(send_msg);
                //刷新RecyclerView显示
                msgAdapter.notifyItemInserted(list.size() - 1);
                recyclerView.scrollToPosition(msgAdapter.getItemCount() - 1);
            } catch (Exception e) {
                //"上传失败");
            }
        }
    }

    /**
     * 从保存原图的地址读取图片
     */
    private String readpic() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/Pictures/" + "UserIcon.png";
        return filePath;
    }

    private void saveImageToLocal(final Bitmap bitmap, String outfile, final String time, final String from, final String to) {
        final File file = new File(outfile);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            flag = 1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread uploadheadpic = new Thread() {
            @Override
            public void run() {
                UploadPicToServer(file,time,from,to);
            }
        };
        uploadheadpic.start();
        try {
            uploadheadpic.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ChatMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String type = intent.getStringExtra("type");
            Log.e("ChatActivity.ChatMessageReceiver",message);
            JSONObject mes = null;
            MsgEntity Msa_receive = null;
            try {
                switch (type)
                {
                    case "text":
                        mes = new JSONObject(message);
                        String content = mes.getJSONObject("text").getString("content");
                        Msa_receive = new MsgEntity(MsgEntity.RCV_MSG,content,MsgEntity.IS_READ);
                        break;
                    case "img":
                        mes = new JSONObject(message);
                        final String time = mes.getString("time");
                        final String from = mes.getJSONObject("text").getString("from");
                        final String to = mes.getJSONObject("text").getString("to");
                        tempUri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/chatImg/"
                                +time+"_"+from+"_"+to+".png";
                        Thread downImg = new Thread()
                        {
                            @Override
                            public void run() {
                                super.run();
                                DownloadPicToServer(time,from,to);
                            }
                        };
                        downImg.start();
                        try {
                            downImg.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Msa_receive = new MsgEntity(MsgEntity.RCV_MSG, "" ,MsgEntity.IS_READ,tempUri,MsgEntity.IS_Img,time);
                        break;
                }
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

    private File DownloadHeadPicFromServer(String uname){
        int statusID = 1;
        //检查并新增头像图片的文件夹及文件
        String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.example.login/files/Pictures/";//获取SDCard目录
        File fileJA = new File(sdCardDir);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(sdCardDir, uname+"Icon.png");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/getHeadpicN";
        URL url;
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());
            conn.setRequestProperty("uname",uname);

            int responseCode = conn.getResponseCode();
            Log.d("Main_Activity","头像下载的返回码为"+responseCode);
            if(responseCode ==200){
                //请求成功 获得返回的流
                InputStream fis = conn.getInputStream();
                byte[] by= new byte[1024];
                int n=0;
                FileOutputStream outStream = null;
                try{
                    outStream = new FileOutputStream(file);
                    while((n = fis.read(by))!=-1)
                    {
                        outStream.write(by,0,n);
                        outStream.flush();
                    }
                    outStream.close();
                    fis.close();
                    Log.d("Main_Activity","下载头像图片成功");
                    return file;
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(outStream!=null){
                        outStream.close();
                    }
                }
            }else {
                //请求失败
                Log.e("Main_Activity","头像下载失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }

    private void UploadPicToServer(File file,String time ,String from ,String to){
        int statusID = 1;
        FileInputStream fis = null;
        StringBuffer sb = null;

        String urlPath="http://www.lovecurry.club:8080/TravelApp/websocket/DoMesPic";
        URL url;
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());
            conn.setRequestProperty("picName", time+"_"+from+"_"+to+".png");
            //conn.setRequestProperty("picName", "123.png");

            OutputStream os=conn.getOutputStream();
            int n = 0;
            try {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, len); //写入图片数据
                }
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d("ChatActivity","头像上传的返回码为"+responseCode);
            if(responseCode ==200){

            }else {
                //请求失败
                Log.e("ChatActivity","头像上传失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private File DownloadPicToServer(String time ,String from ,String to){
        int statusID = 1;
        //检查并新增头像图片的文件夹及文件
        String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.example.login/files/chatImg/";//获取SDCard目录
        File fileJA = new File(sdCardDir);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = null;
        file = new File(fileJA,time+"_"+from+"_"+to+".png");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String urlPath="http://www.lovecurry.club:8080/TravelApp/websocket/getMesPic";
        URL url;
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());
            conn.setRequestProperty("picName", time+"_"+from+"_"+to+".png");
            //conn.setRequestProperty("picName", "123.png");

            int responseCode = conn.getResponseCode();
            Log.d("ChatActivity","图片下载的返回码为"+responseCode);
            if(responseCode ==200){
                InputStream fis = conn.getInputStream();
                byte[] by= new byte[1024];
                int n=0;
                FileOutputStream outStream = null;
                try{
                    outStream = new FileOutputStream(file);
                    while((n = fis.read(by))!=-1)
                    {
                        outStream.write(by,0,n);
                        outStream.flush();
                    }
                    outStream.close();
                    fis.close();
                    Log.d("ChatActivity","下载图片成功");
                    return file;
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(outStream!=null){
                        outStream.close();
                    }
                }

            }else {
                //请求失败
                Log.e("ChatActivity","图片下载失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }
}
