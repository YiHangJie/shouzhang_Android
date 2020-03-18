package com.example.login.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login.Fragment.FirstFragment;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;

/*
① 创建一个继承RecyclerView.Adapter<VH>的Adapter类
② 创建一个继承RecyclerView.ViewHolder的静态内部类
③ 在Adapter中实现3个方法：
   onCreateViewHolder()
   onBindViewHolder()
   getItemCount()
*/
public class RecycleAdapterDome extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<String> list;
    private List<String> picurl;
    private List<String> time;
    private List<String> web;
    private FirstFragment firstFragment;

    private View inflater;

    private int normalType = 0;     // 第一种ViewType，正常的item
    private int footType = 1;       // 第二种ViewType，底部的提示View

    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示

    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler

    //构造方法，传入数据
    public RecycleAdapterDome(FirstFragment firstFragment,Context context, List<String> list, List<String> picurl, List<String> time, List<String> web){
        //初始化变量
        this.firstFragment = firstFragment;
        this.context = context;
        this.list = list;
        this.picurl = picurl;
        this.time = time;
        this.web = web;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //创建ViewHolder，返回每一项的布局
        if (viewType == 0) {
            //你的item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_recycler, viewGroup, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        } else {
            //底部“加载更多”item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.footview, viewGroup, false);
            FootHolder footerHolder = new FootHolder(view);
            return footerHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        //将数据和控件绑定
        // 如果是正常的item，直接设置TextView的值
        if (holder instanceof RecycleAdapterDome.MyViewHolder) {
            ((MyViewHolder) holder).newstitle.setText(list.get(position));

            if(picurl.get(position)!= "")
                Glide.with(context).load(picurl.get(position)).into(((MyViewHolder) holder).pic);
            else
                Glide.with(context).load(R.mipmap.defalutpic).into(((MyViewHolder) holder).pic);        //后台传来的json包里面，可能有些图片的picUrl是无效的，不是空的，以后要改进

            ((MyViewHolder) holder).time.setText(time.get(position));

            ((MyViewHolder) holder).webview.setText(web.get(position));

            //((MyViewHolder) holder).webview.setVisibility();

            ((MyViewHolder) holder).newstitle.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    CharSequence charSequence = ((MyViewHolder) holder).getWeb().getText();
                    String message =(String) charSequence;
                    charSequence = ((MyViewHolder) holder).getNewstitle().getText();
                    String title =(String) charSequence;

                    firstFragment.GetWeb(message,title);
                    Toast.makeText(context, ((MyViewHolder) holder).getNewstitle().getText()+"被点击了",
                            Toast.LENGTH_SHORT).show();
                }
            });

            ((MyViewHolder) holder).pic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    CharSequence charSequence = ((MyViewHolder) holder).getWeb().getText();
                    String message =(String) charSequence;
                    charSequence = ((MyViewHolder) holder).getNewstitle().getText();
                    String title =(String) charSequence;

                    firstFragment.GetWeb(message,title);
                    Toast.makeText(context, ((MyViewHolder) holder).getNewstitle().getText()+"被点击了",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore == true) {
                // 不隐藏footView提示
                fadeTips = false;
                if (list.size() > 0) {
                    // 如果查询数据发现增加之后，就显示正在加载更多
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            } else {
                if (list.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    ((FootHolder) holder).tips.setText("没有更多数据了");

                    // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    //+1是因为有footitem
    @Override
    public int getItemCount() {
        //返回Item总条数
        return list.size()+1;
    }

    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return list.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }

    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        list = new ArrayList<>();
        picurl = new ArrayList<>();
        time = new ArrayList<>();
        web = new ArrayList<>();
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateData(List<String> newDatas, List<String> picurl, List<String> time, List<String> webpath,boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            this.list.addAll(newDatas);
            this.picurl.addAll(picurl);
            this.time.addAll(time);
            this.web.addAll(webpath);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    //内部类，绑定控件
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView newstitle;
        private ImageView pic;
        private TextView time;
        private TextView webview;

        public TextView getNewstitle() {
            return newstitle;
        }
        public ImageView getPic(){ return pic;}
        public TextView gettime(){
            return time;
        }
        public TextView getWeb(){
            return webview;
        }



        public MyViewHolder(View itemView) {
            super(itemView);
            newstitle = (TextView) itemView.findViewById(R.id.newstitle);
            pic = (androidx.appcompat.widget.AppCompatImageView) itemView.findViewById(R.id.pic);
            time = (TextView) itemView.findViewById(R.id.time);
            webview = (TextView)itemView.findViewById(R.id.webpath);
        }

    }

    // // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
        }
    }
}