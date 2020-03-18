package com.example.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.example.login.Adapter.RecycleAdapterDome;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Main_Activity extends AppCompatActivity  {

    public static final String EXTRA_MESSAGE = "Main_EXTRA_MESSAGE";
    public static final String TITLE = "Main_TITLE";
    private static Context context;


    private FloatingActionButton daka;
    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapterDome adapterDome;//声明适配器
    private List<String> title_list;
    private List<String> picurl_list;
    private List<String> time_list;
    private List<String> web_list;
    private static ActionBar actionbar;

    public static int ActionBarHeight;

    //AMap是地图对象
    private AMap aMap;
    private MapView mapView;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private LocationSource.OnLocationChangedListener mListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    public static double wei = 0;
    public static double jing = 0;
    public static String dakatime = "";
    public static String citycode = "110000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        actionbar = getSupportActionBar();
//        if(actionbar != null)
//        {
//            actionbar.hide();
//        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnav);
        NavController navController = Navigation.findNavController(this,R.id.fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(bottomNavigationView.getMenu()).build();
        NavigationUI.setupActionBarWithNavController(this,navController,configuration);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        context = this;
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        title_list = new ArrayList<String>();
//        picurl_list = new ArrayList<String>();
//        time_list = new ArrayList<String>();
//        web_list = new ArrayList<String>();
//
//
//        //初始化数据集合
//        Thread inidata = new Thread()
//        {
//            public void run()
//            {
//                loadmore();
//            }
//        };
//        inidata.start();
//        try {
//            inidata.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        adapterDome = new RecycleAdapterDome(this,context,title_list,picurl_list,time_list,web_list);
//        recyclerView.setAdapter(adapterDome);
//
//        /*
//        与ListView效果对应的可以通过LinearLayoutManager来设置
//        与GridView效果对应的可以通过GridLayoutManager来设置
//        与瀑布流对应的可以通过StaggeredGridLayoutManager来设置
//        */
////        //LinearLayoutManager manager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
////        LinearLayoutManager manager = new LinearLayoutManager(context);
////        manager.setOrientation(RecyclerView.VERTICAL);
////        //RecyclerView.LayoutManager manager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
////        //GridLayoutManager manager1 = new GridLayoutManager(context,2);
////        //manager1.setOrientation(GridLayoutManager.VERTICAL);
////        //StaggeredGridLayoutManager manager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
////        recyclerView.setLayoutManager(manager);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(Main_Activity.this, RecyclerView.VERTICAL,false));
//
//        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//            @Override
//            public void onLoadMore() {
//                //在这里写获取数据的逻辑
//                //...
//
//                Thread getmorenews = new Thread() {
//                    @Override
//                    public void run() {
//                        loadmore();
//                    }
//                };
//                getmorenews.start();
//                try {
//                    getmorenews.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //获取数据后 传入adapter中上面写的更新数据的方法
//                adapterDome.updateData(title_list,picurl_list,time_list,web_list,true);
//            }
//        });
//
//        //获取地图控件引用
//        mapView = (com.amap.api.maps.MapView) findViewById(R.id.map);
//
//        if(ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)//判断权限是否开启,开启的话我们就直接去执行我们需要执行的方法,没有开启的话我们就要提示用户去开启
//                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
//            //开启定位权限,200是标识码
//            ActivityCompat.requestPermissions(Main_Activity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
//        }else{
//            initLocation();//开始定位
//            Toast.makeText(Main_Activity.this,"已开启定位权限",Toast.LENGTH_LONG).show();
//        }
//
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
//        mapView.onCreate(savedInstanceState);
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            //设置显示定位按钮 并且可以点击
//            UiSettings settings = aMap.getUiSettings();
////            aMap.setLocationSource(this);//设置了定位的监听
//            // 是否显示定位按钮
//            settings.setMyLocationButtonEnabled(true);
//            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
//        }
//        //开始定位
//        initLocation();
//
//        AMapLocationListener aMapLocationListener = new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                String citycode = aMapLocation.getCityCode();
//                sendcitycode(citycode);
//                Main_Activity.wei = aMapLocation.getLatitude();//获取纬度
//                Main_Activity.jing = aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                Main_Activity.dakatime = df.toPattern();
//            }
//        };
//
//        daka = (FloatingActionButton) findViewById(R.id.fab);
//
//        daka.setOnClickListener(new View.OnClickListener() {
//            int num;
//            @Override
//            public void onClick(View v) {
//                Thread dakaThread = new Thread(){
//                    @Override
//                    public void run() {
//                       num = sendlocation();
//                    }
//                };
//                dakaThread.start();
//
//                if(num==1)
//                {
//                    Toast.makeText (getApplicationContext(),"打卡成功！", Toast.LENGTH_LONG ).show();
//                }
//                else if(num==0)
//                {
//                    Toast.makeText (getApplicationContext(),"打卡失败！", Toast.LENGTH_LONG ).show();
//                }
//                else if(num==-1)
//                {
//                    Toast.makeText (getApplicationContext(),"提交数据失败！", Toast.LENGTH_LONG ).show();
//                }
//            }
//        });
//
//        Thread citycodetest = new Thread(){
//
//            public void run()
//            {
//                sendcitycode("330104");
//            }
//        };
//        citycodetest.start();
//        try {
//            citycodetest.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if(fragment==null){
            return false;
        }
        return NavHostFragment.findNavController(fragment).navigateUp();
    }

    public ActionBar getActionbar() {
        return this.actionbar;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Main_activity","onRestart()");
        websocket_Manager socketManager = new websocket_Manager();
        socketManager.creat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Main_activity","onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        final String uname_close = websocket_Manager.getUsername();
        Log.e("Main_activity","onStop()");
        Log.e("uname",websocket_Manager.getUsername());
        //activyty被销毁之前向后台发送数据
        Thread closeActivity = new Thread()
        {
            public void run()
            {
                String urlPath="http://47.103.66.24:8080/TravelApp/account/DoClose";
                //    String urlPath="http://192.168.42.207:8080/20170112/login/toJsonMain.action"; 这个是实体机(手机)的端口
                URL url;
                int id = 0 ;
                try {
                    url = new URL(urlPath);

                    String content;

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //开启连接
                    conn.setConnectTimeout(5000);

                    conn.setDoOutput(true);

                    conn.setDoInput(true);

                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("ser-Agent", "Fiddler");

                    conn.setRequestProperty("uname", uname_close);

                    Log.d("uname",websocket_Manager.getUsername());

                    InputStream inputStream = conn.getInputStream();
                    // 调用自己写的NetUtils() 将流转成string类型

                    String json = NetUtils.readString(inputStream);
                    System.out.println(json + "json");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        closeActivity.start();
        try {
            closeActivity.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //websocket_Manager.killwebsocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final String uname_close = websocket_Manager.getUsername();
        Log.e("Main_activity","onDestroy()");
        Log.e("uname",websocket_Manager.getUsername());
        //activyty被销毁之前向后台发送数据
        Thread closeActivity = new Thread()
        {
            public void run()
            {
                String urlPath="http://47.103.66.24:8080/TravelApp/account/DoClose";
                //    String urlPath="http://192.168.42.207:8080/20170112/login/toJsonMain.action"; 这个是实体机(手机)的端口
                URL url;
                int id = 0 ;
                try {
                    url = new URL(urlPath);

                    String content;

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //开启连接
                    conn.setConnectTimeout(5000);

                    conn.setDoOutput(true);

                    conn.setDoInput(true);

                    conn.setRequestMethod("GET");

                    conn.setRequestProperty("ser-Agent", "Fiddler");

                    conn.setRequestProperty("uname", uname_close);

                    Log.d("uname",websocket_Manager.getUsername());

                    InputStream inputStream = conn.getInputStream();
                    // 调用自己写的NetUtils() 将流转成string类型

                    String json = NetUtils.readString(inputStream);
                    System.out.println(json + "json");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        closeActivity.start();
        try {
            closeActivity.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        websocket_Manager.killwebsocket();
    }


    //    private void initLocation(){
//        //声明AMapLocationClient类对象
//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //异步获取定位结果
//        AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation amapLocation) {
//                if (amapLocation != null) {
//                    if (amapLocation.getErrorCode() == 0) {
//                        //解析定位结果
//                        //text_map.setText(amapLocation.getAddress());
//                        String citycode = amapLocation.getCityCode();
//                        sendcitycode(citycode);
//                        Main_Activity.wei = amapLocation.getLatitude();//获取纬度
//                        Main_Activity.jing = amapLocation.getLongitude();//获取经度
//                        amapLocation.getAccuracy();//获取精度信息
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date date = new Date(amapLocation.getTime());
//                        Main_Activity.dakatime = df.format(date);//定位时间
//                    }
//                }
//            }
//        };
//        //设置定位回调监听
//        mLocationClient.setLocationListener(mAMapLocationListener);
//        //启动定位
//
//    }
//
//
//
//    @Override//我们需要写一个回调的方法,onRequestPermissionsResult,在我们需要申请权限的时候就会执行这个回调
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case 200://刚才的识别码
//                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//用户同意权限,执行我们的操作
//                    initLocation();//开始定位
//                }else{//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
//                    Toast.makeText(Main_Activity.this,"未开启定位权限,请手动到设置去开启权限",Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:break;
//        }
//    }
////
////    public static String sHA1(Context context) {
////        try {
////            PackageInfo info = context.getPackageManager().getPackageInfo(
////                    context.getPackageName(), PackageManager.GET_SIGNATURES);
////            byte[] cert = info.signatures[0].toByteArray();
////            MessageDigest md = MessageDigest.getInstance("SHA1");
////            byte[] publicKey = md.digest(cert);
////            StringBuffer hexString = new StringBuffer();
////            for (int i = 0; i < publicKey.length; i++) {
////                String appendString = Integer.toHexString(0xFF & publicKey[i])
////                        .toUpperCase(Locale.US);
////                if (appendString.length() == 1)
////                    hexString.append("0");
////                hexString.append(appendString);
////                hexString.append(":");
////            }
////            String result = hexString.toString();
////            return result.substring(0, result.length()-1);
////        } catch (PackageManager.NameNotFoundException e) {
////            e.printStackTrace();
////        } catch (NoSuchAlgorithmException e) {
////            e.printStackTrace();
////        }
////        return null;
////    }
////
////    private void location() {
////        //初始化定位
////        mLocationClient = new AMapLocationClient(getApplicationContext());
////        //设置定位回调监听
////        mLocationClient.setLocationListener(this);
////        //初始化定位参数
////        mLocationOption = new AMapLocationClientOption();
////        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
////        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
////        //设置是否返回地址信息（默认返回地址信息）
////        mLocationOption.setNeedAddress(true);
////        //设置是否只定位一次,默认为false
////        mLocationOption.setOnceLocation(true);
////        //设置是否强制刷新WIFI，默认为强制刷新
////        mLocationOption.setWifiActiveScan(true);
////        //设置是否允许模拟位置,默认为false，不允许模拟位置
////        mLocationOption.setMockEnable(false);
////        //设置定位间隔,单位毫秒,默认为2000ms
////        mLocationOption.setInterval(2000);
////        //给定位客户端对象设置定位参数
////        mLocationClient.setLocationOption(mLocationOption);
////        //启动定位
////        mLocationClient.startLocation();
////    }
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
////        mapView.onDestroy();
////        mLocationClient.stopLocation();//停止定位
////        mLocationClient.onDestroy();//销毁定位客户端。
////    }
////
////    @Override
////    protected void onResume() {
////        super.onResume();
////        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
////        mapView.onResume();
////    }
////
////    @Override
////    protected void onPause() {
////        super.onPause();
////        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
////        mapView.onPause();
////    }
////
////    @Override
////    protected void onSaveInstanceState(Bundle outState) {
////        super.onSaveInstanceState(outState);
////        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
////        mapView.onSaveInstanceState(outState);
////    }
////
//
//
////    @Override
////    public void activate(OnLocationChangedListener onLocationChangedListener) {
////        mListener = onLocationChangedListener;
////    }
////
////    @Override
////    public void deactivate() {
////        mListener = null;
////    }
//
//    public void loadmore() {
//        // 网络请求
//        String urlPath="http://www.lovecurry.club:8080/TravelApp/news/DoNews";
//        URL url;
//        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
//        try {
//            url=new URL(urlPath);
//            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
//            conn.setConnectTimeout(5000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("ser-Agent", "Fiddler");
//            int code=conn.getResponseCode();
//            System.out.println(code);
//            if(code==200){   //与后台交互成功返回 200
//
//                //读取返回的json数据
//                InputStream inputStream=conn.getInputStream();
//                // 调用自己写的NetUtils() 将流转成string类型
//                String json= NetUtils.readString(inputStream);
//                //System.out.println(json);
//                //System.out.println("Main_Activity json:"+json);
//
//                JSONArray newsarray = new JSONArray(json);
//
//                System.out.println(newsarray.length()+"条news");
//
//                for (int i=0;i<newsarray.length();i++){
//                    String time = newsarray.getJSONObject(i).getString("time");
//                    String description = newsarray.getJSONObject(i).getString("descrption");
//                    String title = newsarray.getJSONObject(i).getString("title");
//                    String picUrl = newsarray.getJSONObject(i).getString("picUrl");
//                    String passageurl = newsarray.getJSONObject(i).getString("url");
//
//                    String utf8_title = new String(title.getBytes(),"utf-8");
//                    String utf8_picurl = new String(picUrl.getBytes(),"utf-8");
//                    String utf8_time = new String(time.getBytes(),"utf-8");
//                    String utf8_url = new String(passageurl.getBytes(), "utf-8");
//                    utf8_time = utf8_time.substring(0,utf8_time.length()-8);
//
//                    title_list.add(utf8_title);
//                    picurl_list.add(utf8_picurl);
//                    time_list.add(utf8_time);
//                    web_list.add(utf8_url);
//                }
////                Gson gson=new Gson();  //引用谷歌的json包
////                User user=gson.fromJson(json,User.class); //谷歌的解析json的方法
////
////                int userID;
////                User user = new User(userID,name,pwd);
////
////                id =user.getId();  //然后user.get你想要的值
////                String username=user.getUsername();
////                String password=user.getPassword();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void sendcitycode(String citycode) {
//        // 网络请求
//        String urlPath="http://www.lovecurry.club:8080/TravelApp/location/DoWeather";
//        String content = "cityCode="+citycode;
//        URL url;
//        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
//        try {
//            url=new URL(urlPath);
//            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
//            conn.setConnectTimeout(5000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("ser-Agent", "Fiddler");
//
//            OutputStream os=conn.getOutputStream();
//            os.write(content.getBytes()); //字符串写进二进流
//            os.flush();
//            os.close();
//
//            int code=conn.getResponseCode();
//            System.out.println(code);
//            if(code==200){   //与后台交互成功返回 200
//
//                //读取返回的json数据
//                InputStream inputStream=conn.getInputStream();
//                // 调用自己写的NetUtils() 将流转成string类型
//                String json= NetUtils.readString(inputStream);
//                //System.out.println(json);
//                System.out.println("Main_Activity json:"+json);
//
//                JSONObject weattherinfo = new JSONObject(json);
//                String weather = weattherinfo.getString("weather");
//                String windpower = weattherinfo.getString("windpower");
//                String city = weattherinfo.getString("city");
//                String temperature = weattherinfo.getString("temperature");
//
//                TextView weatherView = (TextView)findViewById(R.id.weathertype);
//                TextView temperatureView = (TextView)findViewById(R.id.temperature);
//                TextView windpowerView = (TextView)findViewById(R.id.windpower);
//
//                weatherView.setText(weather);
//                temperatureView.setText("温度："+temperature);
//                windpowerView.setText("风力"+windpower);
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void GetWeb(String message,String title)
//    {
//        Intent it = new Intent(this,News_Activity.class);
//        it.putExtra(EXTRA_MESSAGE, message);
//        it.putExtra(TITLE,title);
//        startActivity(it);
//    }
//
//    private int sendlocation() {
//
//        System.out.println(wei);
//        System.out.println(jing);
//        System.out.println(dakatime);
//
//        String urlPath="http://www.lovecurry.club:8080/TravelApp/Location/addCooridinate";
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        Main_Activity.dakatime = df.format(date);
//        String content = "latitude="+Main_Activity.wei+"&longitude="+Main_Activity.jing+"&time="+Main_Activity.dakatime;
//        URL url;
//        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
//        try {
//            url=new URL(urlPath);
//            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
//            conn.setConnectTimeout(5000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("ser-Agent", "Fiddler");
//
//            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());
//            System.out.println("JSESSIONID.getJSESSIONIDNAME():"+JSESSIONID.getJSESSIONIDNAME());
//
//            OutputStream os=conn.getOutputStream();
//            os.write(content.getBytes()); //字符串写进二进流
//            os.flush();
//            os.close();
//
//            int code=conn.getResponseCode();
//            System.out.println(code);
//            if(code==200){   //与后台交互成功返回 200
//
//                //读取返回的json数据
//                InputStream inputStream=conn.getInputStream();
//                // 调用自己写的NetUtils() 将流转成string类型
//                String json= NetUtils.readString(inputStream);
//                //System.out.println(json);
//                System.out.println("Main_Activity json:"+json);
//
//                String status = conn.getHeaderField("status");
//                int dakastatus = Integer.parseInt(status);
//
//                if(dakastatus==200)
//                {
//                    return 1;       //打卡成功
//                }
//                else if(dakastatus==-1)
//                {
//                    return 0;       //打卡失败
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return -1;                  //数据提交失败
//    }

}
