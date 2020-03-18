package com.example.login.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.login.Adapter.Dakarecord_Adapter;
import com.example.login.Fragment.FourthFragment;
import com.example.login.JSESSIONID;
import com.example.login.Model.FourthViewModel;
import com.example.login.NetUtils;
import com.example.login.R;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class Fragment_DakaRecord extends Fragment {

    private ListView listview_dakarecord;

    private LinkedList<String> lon;
    private LinkedList<String> lat;
    private LinkedList<String> mtime;

    private Dakarecord_Adapter dakarecord_adapter;

    private FourthViewModel mViewModel;

    public static FourthFragment newInstance() {
        return new FourthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_dakarecord, container, false);
        //getActivity().getActionBar().hide();
        listview_dakarecord = view.findViewById(R.id.listview_dakarecord);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FourthViewModel.class);
        // TODO: Use the ViewModel

        lon = new LinkedList<String>();
        lat = new LinkedList<String>();
        mtime = new LinkedList<String>();

        setHasOptionsMenu(true);

        Thread getdakainformation = new Thread() {
            @Override
            public void run() {
                dakainformation();
            }
        };
        getdakainformation.start();
        try {
            getdakainformation.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dakarecord_adapter = new Dakarecord_Adapter(lon,lat,mtime,getActivity());

        listview_dakarecord.setAdapter(dakarecord_adapter);
        listview_dakarecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"你点击了第" + position + "项",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void dakainformation() {
//        lon.remove();
//        lat.remove();
//        mtime.remove();
        // 网络请求
        String urlPath="http://www.lovecurry.club:8080/TravelApp/location/showCoordinate";
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

            InputStream inputStream=conn.getInputStream();
            // 调用自己写的NetUtils() 将流转成string类型
            String json= NetUtils.readString(inputStream);
            //System.out.println(json);
            System.out.println("Main_Activity json:"+json);

            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200

//                //读取返回的json数据
//                JSONObject weattherinfo = new JSONObject(json);

//                String latitude = weattherinfo.getString("latitude");
//                String time = weattherinfo.getString("time");
//                String longitude = weattherinfo.getString("longitude");

                JSONArray newsarray = new JSONArray(json);

                System.out.println(newsarray.length()+"条打卡记录");

                for (int i=0;i<newsarray.length();i++){
                    String time = newsarray.getJSONObject(i).getString("time");
                    String latitude = newsarray.getJSONObject(i).getString("latitude");
                    String longitude = newsarray.getJSONObject(i).getString("longitude");

                    lon.add(longitude);
                    lat.add(latitude);
                    mtime.add(time);

//                    title_list.add(utf8_title);
//                    picurl_list.add(utf8_picurl);
//                    time_list.add(utf8_time);
//                    web_list.add(utf8_url);

                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
