package com.example.login.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.Adapter.addFriend_Adapter;
import com.example.login.NetUtils;
import com.example.login.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Fragment_addFriend extends Fragment {

    private Context mContext;
    private addFriend_Adapter mAdapter = null;

    private EditText account;
    private Button search;
    private Button add;
    private String friend_information;
    private ListView possiblefriends;

    int num;

    private List<String> nameList;

    private Handler handler = new Handler(){

        public void handleMessage(android.os.Message msg){

            int what=msg.what;
            switch(what){
                case 0:
                    mAdapter.notifyDataSetChanged();
                    break;
                default :
                    break;
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addfriend, container, false);
        //getActivity().getActionBar().hide();

        mContext = getActivity();
        possiblefriends = view.findViewById(R.id.list_friend);

        account = view.findViewById(R.id.account);
        search = view.findViewById(R.id.查询);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nameList = new LinkedList<String>();

        mAdapter = new addFriend_Adapter((LinkedList<String>) nameList, mContext);
        possiblefriends.setAdapter(mAdapter);

//        possiblefriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mContext, "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
//
//            }
//        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friend_information = account.getText().toString();

                Thread searchfriend = new Thread() {
                    @Override
                    public void run() {
                        num  = searchfirends(friend_information);

                    }
                };
                searchfriend.start();
                try {
                    searchfriend.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Fragment_addFriend", "num="+num);
                if(num==1) {
                    Toast.makeText(mContext, "查询成功！", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(0);

                }else if(num==0)
                {
                    Toast.makeText(mContext, "用户未找到！", Toast.LENGTH_SHORT).show();
                }
                else if(num==-1)
                {
                    Toast.makeText(mContext, "数据提交失败！", Toast.LENGTH_SHORT).show();
                }
                searchfriend.interrupt();
            }
        });
    }



    int searchfirends(String information)
    {
        // 网络请求
        String urlPath="http://www.lovecurry.club:8080/TravelApp/friend/SearchUser";
        String content = "uname="+information;
        URL url;
        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");

            OutputStream os=conn.getOutputStream();
            os.write(content.getBytes()); //字符串写进二进流
            os.flush();
            os.close();

            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200

                //读取返回的json数据
                InputStream inputStream=conn.getInputStream();
                // 调用自己写的NetUtils() 将流转成string类型
                String json= NetUtils.readString(inputStream);
                //System.out.println(json);
                System.out.println("Main_Activity json:"+json);

                if(json.equals("用户未找到"))
                {
                    return 0;
                }

                JSONObject friendinfo = new JSONObject(json);
                String friendsname = friendinfo.getString("uname");
                Log.d("Fragment_addFriend", "friendsname:"+friendsname);
                String picUrl = friendinfo.getString("headpicUrl");
                Log.d("Fragment_addFriend", "picUrl:"+picUrl);


                if(picUrl==null)
                {
                    picUrl = "";
                }

                Log.d("Fragment_addFriend", "1");

                nameList.clear();
                nameList.add(friendsname);

                Log.d("Fragment_addFriend", "2");
                return 1;
            }
            else
            {
                return -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}