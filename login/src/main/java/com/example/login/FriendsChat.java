//package com.example.login;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.LinkedList;
//import java.util.List;
//
//public class FriendsChat extends AppCompatActivity {
//    private List<Animal> mData = null;
//    private Context mContext;
//    private AnimalAdapter mAdapter = null;
//    private ListView list_animal;
//    private LinearLayout ly_content;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friendschat);
//
//        mContext = FriendsChat.this;
//        list_animal = (ListView) findViewById(R.id.list_animal);
//
//        //动态加载顶部View和底部View
//        final LayoutInflater inflater = LayoutInflater.from(this);
////        View headView = inflater.inflate(R.layout.view_header, null, false);
////        View footView = inflater.inflate(R.layout.view_footer, null, false);
//
//        mData = new LinkedList<Animal>();
//        mData.add(new Animal("狗说", "你是狗么?"));
//        mData.add(new Animal("牛说", "你是牛么?"));
//        mData.add(new Animal("鸭说", "你是鸭么?"));
//        mData.add(new Animal("鱼说", "你是鱼么?"));
//        mData.add(new Animal("马说", "你是马么?"));
//
//        mAdapter = new AnimalAdapter( (LinkedList<Animal>)mData, mContext);
//        //添加表头和表尾需要写在setAdapter方法调用之前！！！
////        list_animal.addHeaderView(headView);
////        list_animal.addFooterView(footView);
//
//        list_animal.setAdapter(mAdapter);
//        list_animal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mContext,"你点击了第" + position + "项",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }
//}
