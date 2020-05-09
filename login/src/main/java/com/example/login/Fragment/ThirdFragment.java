package com.example.login.Fragment;

import androidx.annotation.MainThread;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
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

import com.example.login.Activity.Main_Activity;
import com.example.login.Adapter.AnimalAdapter;
import com.example.login.Friend;
import com.example.login.JSESSIONID;
import com.example.login.Model.ThirdViewModel;
import com.example.login.NetUtils;
import com.example.login.R;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ThirdFragment extends Fragment{

    private ThirdViewModel mViewModel;

    public static List<Friend> mData = null;
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

        mData = Main_Activity.friendList;

        mAdapter = new AnimalAdapter((LinkedList<Friend>) mData, mContext);
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
    public void onResume() {
        super.onResume();
        Log.d("ThirdFragment","----onResume()----");
//        Thread getheadpic = new Thread(){
//            @Override
//            public void run() {
//                for(int i = 0;i<mData.size();i++)
//                {
//                    DownloadHeadPicFromServer(mData.get(i).getaName());
//                }
//            }
//        };
//        getheadpic.start();
//        try {
//            getheadpic.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
        mAdapter.notifyDataSetChanged();
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


}
