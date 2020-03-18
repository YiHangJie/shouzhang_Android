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

import com.example.login.R;
import com.example.login.websocket_Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class addFriend_Adapter extends BaseAdapter {
    private final LinkedList<String> mData;
    private Context mContext;

    public addFriend_Adapter(LinkedList<String> mData, Context mContext) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_possiblefriend,parent,false);

        TextView friend_name = (TextView) convertView.findViewById(R.id.friend_name);

        friend_name.setText(mData.get(position));

        Button add_action = convertView.findViewById(R.id.add_action);

        add_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "添加好友中。。。。。。", Toast.LENGTH_SHORT).show();

                Map<String , String> confirmation = new HashMap<String, String>();
                confirmation.put("type","好友请求");

                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                Date cur = new Date();
                long temp = cur.getTime();
                String now = simpleDateFormat.format(temp);
                confirmation.put("time",now);

                Map<String,String> text = new HashMap<String, String>();
                text.put("from", websocket_Manager.getUsername());
                text.put("to", mData.get(position));

                JSONObject message1 = new JSONObject(text);
                Log.d("发送好友请求","message1.toString()"+message1.toString());
                JSONObject message2 = new JSONObject(confirmation);
                try {
                    message2.put("text",message1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                websocket_Manager.said(message2.toString());
            }
        });

        return convertView;
    }

}
