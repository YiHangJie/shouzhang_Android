package com.example.login.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
    private Context mCotext;

    private TextView user_name;
    private Button personalsetting;
    private Button record;
    private Button log_out;
    private ImageView HeadPic;

    private File headpic;

    public static FourthFragment newInstance() {
        return new FourthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fourth_fragment, container, false);
        mCotext = getActivity();
        personalsetting = (Button)view.findViewById(R.id.个人设置);
        record=(Button)view.findViewById(R.id.历史纪录);//每个按钮都要这样写一遍
        log_out = view.findViewById(R.id.退出登陆);
        HeadPic = view.findViewById(R.id.h_head);
        user_name = view.findViewById(R.id.user_name);
        user_name.setText(websocket_Manager.getUsername());

//        //更新头像
//        Thread GetFriendsName = new Thread() {
//            @Override
//            public void run() {
//                headpic = DownloadHeadPicFromServer(websocket_Manager.getUsername());
//        }
//        };
//        GetFriendsName.start();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FourthViewModel.class);

        personalsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "前往个人设置", Toast.LENGTH_SHORT).show();
                NavHostFragment
                        .findNavController(getParentFragment())
                        .navigate(R.id.action_fourthfragment_to_fragment_setRecord);
            }
        });


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

        headpic = new File(readheadpic());
        //更新头像
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        Glide.with(mCotext)
                .load(headpic)
                .apply(requestOptions)
                .into(HeadPic);
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
                    inputStream.close();
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

    private File DownloadHeadPicFromServer(String uname){
        int statusID = 1;
        //检查并新增头像图片的文件夹及文件
        String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.example.login/files/Pictures/";//获取SDCard目录
        File fileJA = new File(sdCardDir);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = null;
        if(uname.equals(websocket_Manager.getUsername()))
        {
            file = new File(sdCardDir, "UserIcon.png");
        }
        else
        {
            file = new File(sdCardDir, uname+"Icon.png");
        }

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
}
