package com.example.login.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.Animal;
import com.example.login.ChatActivity;
import com.example.login.R;

import java.util.LinkedList;

public class AnimalAdapter extends BaseAdapter implements View.OnClickListener{
    private LinkedList<Animal> mData;
    private Context mContext;

    public AnimalAdapter(LinkedList<Animal> mData, Context mContext) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_firendschat,parent,false);
        //ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        TextView txt_aName = (TextView) convertView.findViewById(R.id.txt_aName);
        //extView txt_aSpeak = (TextView) convertView.findViewById(R.id.txt_aSpeak);
        //img_icon.setBackgroundResource(mData.get(position).getaIcon());
        txt_aName.setText(mData.get(position).getaName());
        //txt_aSpeak.setText(mData.get(position).getaSpeak());
        txt_aName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
                Log.d("ThirdFragment","OnItemClicked回调函数");
                Intent intent = new Intent(mContext, ChatActivity.class);
                String message = mData.get(position).getaName();
                intent.putExtra("friend's name", message);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }
}
