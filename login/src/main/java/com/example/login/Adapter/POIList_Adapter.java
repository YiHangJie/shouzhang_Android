package com.example.login.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.JSESSIONID;
import com.example.login.LocationAddressInfo;
import com.example.login.Activity.Main_Activity;
import com.example.login.NetUtils;
import com.example.login.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class POIList_Adapter extends BaseAdapter {
    private LinkedList<LocationAddressInfo> mData;
    private Context mContext;
    private int num;

    public POIList_Adapter(LinkedList<LocationAddressInfo> mData, Context mContext) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_poi_listview,parent,false);
        //ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView location = (TextView) convertView.findViewById(R.id.locationinfo);
        //img_icon.setBackgroundResource(mData.get(position).getaIcon());
        description.setText(mData.get(position).getTitle());
        location.setText(mData.get(position).getAddress());
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lon = mData.get(position).getLon();
                String lat = mData.get(position).getLat();
                Main_Activity.jing = Double.parseDouble(lon);
                Main_Activity.wei = Double.parseDouble(lat);

                Thread dakaThread = new Thread(){
                    @Override
                    public void run() {
                        num = sendlocation(mData.get(position));
                        //websocket_Manager.killwebsocket();
                    }
                };
                dakaThread.start();
                try {
                    dakaThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(num==1)
                {
                    Toast.makeText (mContext,"打卡成功！", Toast.LENGTH_LONG ).show();

                }
                else if(num==0)
                {
                    Toast.makeText (mContext,"打卡失败！", Toast.LENGTH_LONG ).show();
                }
                else if(num==-1)
                {
                    Toast.makeText (mContext,"提交数据失败！", Toast.LENGTH_LONG ).show();
                }
                else if(num==20)
                {
                    Toast.makeText (mContext,"线程异常！", Toast.LENGTH_LONG ).show();
                }
            }
        });
        return convertView;
    }

    private int sendlocation(LocationAddressInfo loc) {

        int statusID = 1;

        String urlPath="http://www.lovecurry.club:8080/TravelApp/location/addCooridinate";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Main_Activity.dakatime = df.format(date);
        String content = "latitude="+ loc.getLat()+"&longitude="+ loc.getLon()+"&time="+ df.format(date)
                +"&title="+loc.getTitle()+"&address="+loc.getAddress()+"&typedes="+loc.getTypedes()+"&typecode="+loc.getTypecode();
        Log.e("POIList_Adapter","打卡内容："+content);
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
            System.out.println("JSESSIONID.getJSESSIONIDNAME():"+JSESSIONID.getJSESSIONIDNAME());

            OutputStream os=conn.getOutputStream();
            os.write(content.getBytes("UTF-8")); //字符串写进二进流
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

                String status = conn.getHeaderField("status");
                int dakastatus = Integer.parseInt(status);
                Log.e("dakastatus: ","String Status = "+status+" int dakastatus = "+dakastatus);

                inputStream.close();

                if(dakastatus==200)
                {
                    statusID  = 1;
                    return statusID;       //打卡成功
                }
                else if(dakastatus==-1)
                {
                    statusID  = 0;
                    return statusID;       //打卡失败
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        statusID  = -1;
        return statusID;                  //数据提交失败
    }
}
