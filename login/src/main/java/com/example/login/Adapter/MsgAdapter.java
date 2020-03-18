package com.example.login.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MsgEntity;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<MsgEntity> mMsg;//消息的实体类集合
    private Context mContext;
    public MsgAdapter(Context context, List<MsgEntity> msg){
        this.mMsg=msg;
        this.mContext=context;
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
            holder.rev_layout.setVisibility(View.VISIBLE);
            holder.rev_tv.setText(msg.getContent());
        }
        else if (msg.getType()==MsgEntity.SEND_MSG){
            //发送消息:让接收消息有关的控件隐藏
            holder.rev_layout.setVisibility(View.GONE);
            holder.send_layout.setVisibility(View.VISIBLE);
            holder.send_tv.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsg.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout rev_layout;
        LinearLayout send_layout;
        TextView rev_tv;
        TextView send_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            rev_layout=itemView.findViewById(R.id.rev_layout);
            send_layout=itemView.findViewById(R.id.send_layout);
            rev_tv=itemView.findViewById(R.id.rev_tv);
            send_tv=itemView.findViewById(R.id.send_tv);
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
