package com.example.login.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.login.Activity.ChatActivity;
import com.example.login.MsgEntity;
import com.example.login.R;

import java.io.File;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class  MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<MsgEntity> mMsg;//消息的实体类集合
    private Context mContext;
    private String fname;
    public MsgAdapter(Context context, List<MsgEntity> msg,String fname){
        this.mMsg=msg;
        this.mContext=context;
        this.fname = fname;
    }
    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_chat_recycler,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgAdapter.ViewHolder holder, int position) {
        MsgEntity msg=mMsg.get(position);
        if (msg.getType()== MsgEntity.RCV_MSG){
            //接受消息:让发送消息有关的控件隐藏
            holder.send_layout.setVisibility(View.GONE);
            holder.sendImg_layout.setVisibility(View.GONE);
            if(msg.getIS_Img()==MsgEntity.IS_Img)
            {
                File img = new File(msg.getUrl());
                holder.rev_layout.setVisibility(View.GONE);
                holder.revImg_layout.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(img)
                        .into(holder.rev_img);
            }
            else
            {
                holder.revImg_layout.setVisibility(View.GONE);
                holder.rev_tv.setText(msg.getContent());
            }
        }
        else if (msg.getType()==MsgEntity.SEND_MSG){
            //发送消息:让接收消息有关的控件隐藏
            holder.rev_layout.setVisibility(View.GONE);
            holder.revImg_layout.setVisibility(View.GONE);
            if(msg.getIS_Img()==MsgEntity.IS_Img)       //是图片消息
            {
                File img = new File(msg.getUrl());
                holder.send_layout.setVisibility(View.GONE);
                holder.sendImg_layout.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(img)
                        .into(holder.send_img);
            }
            else                                       //非图片消息
            {
                holder.sendImg_layout.setVisibility(View.GONE);
                holder.send_tv.setText(msg.getContent());
            }
        }
        String upath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/Pictures/" + "UserIcon.png";
        String fpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/Pictures/" +fname+"Icon.png";
        File p1 = new File(upath);
        File p2 = new File(fpath);
        Glide.with(ChatActivity.mContext)
                .load(p1)
                .into(holder.uheadpic);
        Glide.with(ChatActivity.mContext)
                .load(p2)
                .into(holder.fheadpic);
        Glide.with(ChatActivity.mContext)
                .load(p1)
                .into(holder.Img_uheadpic);
        Glide.with(ChatActivity.mContext)
                .load(p2)
                .into(holder.Img_fheadpic);
    }

    @Override
    public int getItemCount() {
        return mMsg.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout rev_layout;
        LinearLayout send_layout;
        LinearLayout revImg_layout;
        LinearLayout sendImg_layout;
        TextView rev_tv;
        TextView send_tv;
        ImageView fheadpic;
        ImageView uheadpic;
        ImageView Img_fheadpic;
        ImageView Img_uheadpic;
        ImageView rev_img;
        ImageView send_img;
        public ViewHolder(View itemView) {
            super(itemView);
            rev_layout=itemView.findViewById(R.id.rev_layout);
            send_layout=itemView.findViewById(R.id.send_layout);
            revImg_layout=itemView.findViewById(R.id.revImg_layout);
            sendImg_layout=itemView.findViewById(R.id.sendImg_layout);

            rev_tv=itemView.findViewById(R.id.rev_tv);
            send_tv=itemView.findViewById(R.id.send_tv);

            fheadpic = itemView.findViewById(R.id.fheadpic);
            uheadpic = itemView.findViewById(R.id.uheadpic);
            Img_fheadpic = itemView.findViewById(R.id.Img_fheadpic);
            Img_uheadpic = itemView.findViewById(R.id.Img_uheadpic);

            rev_img = itemView.findViewById(R.id.rev_img);
            send_img = itemView.findViewById(R.id.send_img);
        }
    }

    public void updateData(List<MsgEntity> mMsg) {
        // 在原有的数据之上增加新数据
        if (mMsg != null) {
            this.mMsg = mMsg;
        }
        notifyDataSetChanged();
    }
}
