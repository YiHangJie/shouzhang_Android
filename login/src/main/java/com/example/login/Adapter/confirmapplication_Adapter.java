package com.example.login.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.Fragment.Fragment_confirmapplication;
import com.example.login.JSESSIONID;
import com.example.login.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class confirmapplication_Adapter extends BaseAdapter {
    private LinkedList<String> mData;
    private Context mContext;

    int num;//状态码

    public confirmapplication_Adapter(LinkedList<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friendapplication,parent,false);

        final TextView friend_name = (TextView) convertView.findViewById(R.id.friend_name);

        friend_name.setText(mData.get(position));

        final String name = mData.get(position);

        Button confirm_action = convertView.findViewById(R.id.confirm_action);
        Button cancel_action = convertView.findViewById(R.id.cancel_action);

        confirm_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread doConfirm = new Thread() {
                    @Override
                    public void run() {
                        Log.e("accept_reject","确认添加为好友");
                        num = accept_reject(name,1);
                    }
                };
                doConfirm.start();
                try {
                    doConfirm.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(num==1)
                {
                    Toast.makeText (mContext,"接受成功", Toast.LENGTH_LONG ).show();
                    Fragment_confirmapplication.nameList.remove(name);
                }
                else if (num==-1)
                {
                    Toast.makeText (mContext,"数据提交失败", Toast.LENGTH_LONG ).show();
                }

            }
        });

        cancel_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread doConfirm = new Thread() {
                    @Override
                    public void run() {
                        Log.e("accept_reject","拒绝添加为好友");
                        num = accept_reject(name,0);
                    }
                };
                doConfirm.start();
                try {
                    doConfirm.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(num==1)
                {
                    Toast.makeText (mContext,"拒绝成功", Toast.LENGTH_LONG ).show();
                    Fragment_confirmapplication.nameList.remove(name);
                }
                else if (num==-1)
                {
                    Toast.makeText (mContext,"数据提交失败", Toast.LENGTH_LONG ).show();
                }

            }
        });

        return convertView;
    }

    int accept_reject(String information,int signal)            //signal 为1 时， 为接受好友申请， 0时为拒绝好友申请
    {
        // 网络请求
        String urlPath="http://www.lovecurry.club:8080/TravelApp/friend/doFriend";
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
            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());
            conn.setRequestProperty("ifAdd", Integer.toString(signal));
            conn.setRequestProperty("who", information);

            OutputStream os=conn.getOutputStream();
            os.write(content.getBytes()); //字符串写进二进流
            os.flush();
            os.close();

            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200
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
