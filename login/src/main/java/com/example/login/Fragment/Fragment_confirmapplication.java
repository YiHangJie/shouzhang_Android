package com.example.login.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.Adapter.confirmapplication_Adapter;
import com.example.login.R;

import java.util.LinkedList;
import java.util.List;

public class Fragment_confirmapplication extends Fragment {

    private Context mContext;
    private confirmapplication_Adapter mAdapter = null;

    private ListView list_friendapplication;

    public static List<String> nameList = new LinkedList<String>();

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

        Log.e("Fragment_confirmapplication","onCreatView()");

        View view = inflater.inflate(R.layout.fragment_confirmapplication, container, false);
        //getActivity().getActionBar().hide();

        mContext = getActivity();

        list_friendapplication = view.findViewById(R.id.list_friendapplication);

        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e("Fragment_confirmapplication","onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        Log.e("Fragment_confirmapplication","nameList.size() = "+nameList.size());
        mAdapter = new confirmapplication_Adapter((LinkedList<String>) nameList, mContext);
        list_friendapplication.setAdapter(mAdapter);

//        possiblefriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mContext, "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }
}