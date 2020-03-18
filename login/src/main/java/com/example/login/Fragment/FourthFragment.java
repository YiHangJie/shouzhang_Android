package com.example.login.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.example.login.Model.FourthViewModel;
import com.example.login.NetUtils;
import com.example.login.R;
import com.example.login.login;
import com.example.login.websocket_Manager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FourthFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "FourthFragment_EXTRA_MESSAGE";

    private FourthViewModel mViewModel;

    private Button record;
    private Button log_out;

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

}
