package com.example.login.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.login.Adapter.AnimalAdapter;
import com.example.login.Animal;
import com.example.login.JSESSIONID;
import com.example.login.Model.ThirdViewModel;
import com.example.login.NetUtils;
import com.example.login.R;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ThirdFragment extends Fragment{

    private ThirdViewModel mViewModel;

    private List<Animal> mData = null;
    private Context mContext;
    private AnimalAdapter mAdapter = null;
    private ListView list_animal;
    private LinearLayout ly_content;

    private Button addFriedns;
    private Button confirmApplication;

    public static String EXTRA_MESSAGE = "ThirdFragment";

    public static ThirdFragment newInstance() {
        return new ThirdFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.third_fragment, container, false);

        mContext = getActivity();
        list_animal = view.findViewById(R.id.list_animal);

        ly_content = view.findViewById(R.id.ly_content);

        mData = new LinkedList<Animal>();

        Thread GetFriendsName = new Thread() {
            @Override
            public void run() {
                getFriendsName();
                Log.e("ThirdFragment", "获取好友列表");
            }
        };
        GetFriendsName.start();
        try {
            GetFriendsName.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mAdapter = new AnimalAdapter((LinkedList<Animal>) mData, mContext);
        list_animal.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ThirdViewModel.class);
        // TODO: Use the ViewModel

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.friendstopmenu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_msg:

                Toast.makeText(getActivity(), "前往添加好友", Toast.LENGTH_SHORT).show();
                //Navigation.findNavController(navcon).navigate(R.id.action_thirdfragment_to_fragment_addFriend);
                NavHostFragment
                        .findNavController(getParentFragment())
                        .navigate(R.id.action_thirdfragment_to_fragment_addFriend);
//                View popupView = getActivity().getLayoutInflater().inflate(R.layout.popupwindow, null);
//                final PopupWindow window = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//                addFriedns = popupView.findViewById(R.id.btn_addfriends);
//                addFriedns.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (window.isShowing()) {
//                            window.dismiss();
//                        }
//                        Toast.makeText(getActivity(), "前往添加好友", Toast.LENGTH_SHORT).show();
//                        //Navigation.findNavController(navcon).navigate(R.id.action_thirdfragment_to_fragment_addFriend);
//                        NavHostFragment
//                                .findNavController(getParentFragment())
//                                .navigate(R.id.action_thirdfragment_to_fragment_addFriend);
//                    }
//                });
//
//
//
//                confirmApplication = popupView.findViewById(R.id.btn_confirmapplication);
//                confirmApplication.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (window.isShowing()) {
//                            window.dismiss();
//                        }
//                        Toast.makeText(getActivity(), "前往好友申请列表", Toast.LENGTH_SHORT).show();
//                        NavHostFragment
//                                .findNavController(getParentFragment())
//                                .navigate(R.id.action_thirdfragment_to_fragment_confirmapplication);
//                    }
//                });
//
//                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
//                window.setFocusable(true);
//                window.setOutsideTouchable(true);
//                window.update();
//
//                //设置显示位置
//                //window.showAsDropDown(anchor , 0, 0);//msgView就是我们menu中的btn_msg
//                window.showAtLocation(ly_content,Gravity.TOP|Gravity.END,0,220);

                break;
            case R.id.btn_con:

                Toast.makeText(getActivity(), "前往好友申请列表", Toast.LENGTH_SHORT).show();
                NavHostFragment
                        .findNavController(getParentFragment())
                        .navigate(R.id.action_thirdfragment_to_fragment_confirmapplication);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getFriendsName() {

        // 网络请求
        String urlPath="http://www.lovecurry.club:8080/TravelApp/DoGetFriend";
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

                System.out.println(newsarray.length()+"个好友");

                for (int i=0;i<newsarray.length();i++){
                    //String uname = newsarray.getJSONObject(i).getString("uname");
                    String uname = newsarray.getString(i);
//                    title_list.add(utf8_title);
//                    picurl_list.add(utf8_picurl);
//                    time_list.add(utf8_time);
//                    web_list.add(utf8_url);
                    mData.add(new Animal(uname,""));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
