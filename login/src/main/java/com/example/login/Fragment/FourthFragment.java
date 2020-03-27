package com.example.login.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.example.login.JSESSIONID;
import com.example.login.Model.FourthViewModel;
import com.example.login.NetUtils;
import com.example.login.R;
import com.example.login.Activity.UpdateHeadPic_Activity;
import com.example.login.Activity.login;
import com.example.login.websocket_Manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FourthFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "FourthFragment_EXTRA_MESSAGE";

    private FourthViewModel mViewModel;

    private TextView user_name;
    private Button record;
    private Button log_out;
    private ImageView HeadPic;

    public static FourthFragment newInstance() {
        return new FourthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fourth_fragment, container, false);
        //getActivity().getActionBar().hide();
        record=(Button)view.findViewById(R.id.历史纪录);//每个按钮都要这样写一遍
        log_out = view.findViewById(R.id.退出登陆);
        HeadPic = view.findViewById(R.id.h_head);
        user_name = view.findViewById(R.id.user_name);
        user_name.setText(websocket_Manager.getUsername());
        Thread downloadheadpic = new Thread() {
            @Override
            public void run() {
                DownloadHeadPictoServer();
            }
        };
        downloadheadpic.start();
        try {
            downloadheadpic.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //更新头像
        String file= readheadpic();
        Bitmap pic = BitmapFactory.decodeFile(file);
        if(pic!=null&&HeadPic!=null)
        {
            HeadPic.setImageBitmap(pic);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FourthViewModel.class);
        // TODO: Use the ViewModel

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到演示界面
//                Intent intent=new Intent(FourthFragment.this,Record_pageActivity.class);
//                startActivity(intent);
                Toast.makeText(getActivity(), "前往查看打卡记录", Toast.LENGTH_SHORT).show();
                NavHostFragment
                        .findNavController(getParentFragment())
                        .navigate(R.id.action_fourthfragment_to_dakaRecord_Fragment);
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到演示界面
//                Intent intent=new Intent(FourthFragment.this,Log_outActivity.class);
//                startActivity(intent);
                Toast.makeText(getActivity(), "退出中", Toast.LENGTH_SHORT).show();
                Dolog_out();
            }
        });

        HeadPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "前往修改头像", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), UpdateHeadPic_Activity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("FourthFragment","----onResume()----");
        //更新头像
        String file= readheadpic();
        Bitmap pic = BitmapFactory.decodeFile(file);
        if(pic!=null&&HeadPic!=null)
        {
            HeadPic.setImageBitmap(pic);
        }
    }

    private String readheadpic() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/Pictures/" + "UserIcon.png";
        return filePath;
    }


    public void Dolog_out()
    {
        final String uname_close = websocket_Manager.getUsername();
        Log.e("FourthFragment","退出登陆");
        Log.e("uname",websocket_Manager.getUsername());

        Thread closeActivity = new Thread()
        {
            public void run()
            {
                String urlPath="http://47.103.66.24:8080/TravelApp/account/DoClose";
                //    String urlPath="http://192.168.42.207:8080/20170112/login/toJsonMain.action"; 这个是实体机(手机)的端口
                URL url;
                int id = 0 ;
                try {
                    url = new URL(urlPath);

                    String content;

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //开启连接
                    conn.setConnectTimeout(5000);

                    conn.setDoOutput(true);

                    conn.setDoInput(true);

                    conn.setRequestMethod("GET");

                    conn.setRequestProperty("ser-Agent", "Fiddler");

                    conn.setRequestProperty("uname", uname_close);

                    Log.d("uname",websocket_Manager.getUsername());

                    InputStream inputStream = conn.getInputStream();
                    // 调用自己写的NetUtils() 将流转成string类型

                    String json = NetUtils.readString(inputStream);
                    System.out.println(json + "json");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        closeActivity.start();
        try {
            closeActivity.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        websocket_Manager.killwebsocket();

        Intent intent = new Intent(getActivity() , login.class);
        String message = "退出登陆";
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void DownloadHeadPictoServer(){
        int statusID = 1;
        //检查并新增头像图片的文件夹及文件
        String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.example.login/files/Pictures/";//获取SDCard目录
        File fileJA = new File(sdCardDir);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(sdCardDir, "UserIcon.png");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/getHeadPic";
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
            System.out.println("JSESSIONID.getJSESSIONIDNAME():"+JSESSIONID.getJSESSIONIDNAME());

            int responseCode = conn.getResponseCode();
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
                    Log.d("FourthFragment","下载头像图片成功");
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(outStream!=null){
                        outStream.close();
                    }
                }
            }else {
                //请求失败
                Log.e("FourthFragment","头像下载失败");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
