package com.example.login.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.login.R;

import java.util.LinkedList;

public class Dakarecord_Adapter extends BaseAdapter {
    private LinkedList<String> mlon;
    private LinkedList<String> mlat;
    private LinkedList<String> mtime;
    private Context mContext;

    public Dakarecord_Adapter(LinkedList<String> lon, LinkedList<String> lat,LinkedList<String> time,Context mContext) {
        this.mlon = lon;
        this.mlat = lat;
        this.mtime = time;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mlon.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dakarecord,parent,false);
        //ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        TextView lon = (TextView) convertView.findViewById(R.id.longitude);
        TextView lat = (TextView) convertView.findViewById(R.id.latitude);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        //img_icon.setBackgroundResource(mData.get(position).getaIcon());
        lon.setText("经度"+mlon.get(position).toString());
        lat.setText("纬度"+mlat.get(position).toString());
        time.setText("时间"+mtime.get(position).toString());
        return convertView;
    }
}
