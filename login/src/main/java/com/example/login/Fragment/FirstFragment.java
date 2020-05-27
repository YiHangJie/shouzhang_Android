package com.example.login.Fragment;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.login.Adapter.POIList_Adapter;
import com.example.login.EndlessRecyclerOnScrollListener;
import com.example.login.JSESSIONID;
import com.example.login.LocationAddressInfo;
import com.example.login.Activity.Main_Activity;
import com.example.login.Model.FirstViewModel;
import com.example.login.NetUtils;
import com.example.login.Activity.News_Activity;
import com.example.login.R;
import com.example.login.Adapter.RecycleAdapterDome;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstFragment extends Fragment implements AMapLocationListener, PoiSearch.OnPoiSearchListener {

    private FirstViewModel mViewModel;

    public static final String EXTRA_MESSAGE = "Main_EXTRA_MESSAGE";
    public static final String TITLE = "Main_TITLE";

    private FrameLayout frameLayout;
    private FloatingActionButton daka;
    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapterDome adapterDome;//声明适配器
    private POIList_Adapter poiList_adapter;
    private ListPopupWindow mListPopupWindow;
    private Context context;
    private List<String> title_list;
    private List<String> picurl_list;
    private List<String> time_list;
    private List<String> web_list;

    //天气控件
    private TextView weatherView ;
    private TextView temperatureView ;
    private TextView windpowerView ;

    String weather = null;
    String windpower = null;
    String weather_city = null;
    String temperature = null;


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

    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private int Pagesize = 0;//搜索POI时设置的结果页面条数
    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiResult poiResult; // 返回的推荐POI结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private TextView informationhint;
    private LinkedList<LocationAddressInfo> data;//自己创建的数据集合

    private LinkedList<Marker> markers = new LinkedList<Marker>();//地图上的图钉对象列表


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0)
            {
                weatherView.setText(weather_city+": "+weather);
                temperatureView.setText("温度："+temperature);
                windpowerView.setText("风力: "+windpower);
            }
        }
    };


    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment,container,false);

        context = getActivity();
        frameLayout = view.findViewById(R.id.firstfragment_framelayout);

        data = new LinkedList<LocationAddressInfo>();

        recyclerView = view.findViewById(R.id.recycler_view);
        if(recyclerView==null)
        {
            Log.e("FirstFragment", "recyclerView null 错误！");
        }
        title_list = new ArrayList<String>();
        picurl_list = new ArrayList<String>();
        time_list = new ArrayList<String>();
        web_list = new ArrayList<String>();

        //获取地图控件引用
        mapView = view.findViewById(R.id.map);

        daka = view.findViewById(R.id.fab);

        weatherView = view.findViewById(R.id.weathertype);
        temperatureView = view.findViewById(R.id.temperature);
        windpowerView = view.findViewById(R.id.windpower);

        informationhint = view.findViewById(R.id.informationhint);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FirstViewModel.class);
        // TODO: Use the ViewModel

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
//            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); //和 setPosition()方法冲突，只设置缩放大小时使用
//            aMap.setInfoWindowAdapter(this);//设置地点的infowindow的适配器

//            AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
//                // marker 对象被点击时回调的接口
//                // 返回 true 则表示接口已响应事件，否则返回false
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//
//                    return true;
//                }
//            };
//            aMap.setOnMarkerClickListener(markerClickListener);
        }

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        /* myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
        fromResource(R.mipmap.btn_voice_map_navi));// 自定义定位蓝点图标*/
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);


        //初始化资讯数据集合
        Thread inidata = new Thread()
        {
            public void run()
            {
                loadmore();
            }
        };
        inidata.start();

        adapterDome = new RecycleAdapterDome(this,context,title_list,picurl_list,time_list,web_list);
        if(adapterDome==null)
        {
            Log.e("FirstFragment", "AdapterDome null 错误！");
        }
        recyclerView.setAdapter(adapterDome);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                //在这里写获取数据的逻辑
                //...

                Thread getmorenews = new Thread() {
                    @Override
                    public void run() {
                        loadmore();
                    }
                };
                getmorenews.start();
                try {
                    getmorenews.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getmorenews.interrupt();
                //获取数据后 传入adapter中上面写的更新数据的方法
                adapterDome.updateData(title_list,picurl_list,time_list,web_list,true);
            }
        });

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)//判断权限是否开启,开启的话我们就直接去执行我们需要执行的方法,没有开启的话我们就要提示用户去开启
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }else{
            initLocation();//开始定位
            Toast.makeText(getActivity(),"已开启定位权限",Toast.LENGTH_LONG).show();
        }


        daka.setOnClickListener(new View.OnClickListener() {
            int num = 20;
            @Override
            public void onClick(View v) {
                doSearchQuery("110000|050000|060000|070000|080000|100000|120000" );
//                View popupView = getActivity().getLayoutInflater().inflate(R.layout.poi_listview, null);
//                final PopupWindow window = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
//                window.setFocusable(true);
//                window.setOutsideTouchable(true);
//                window.update();
//
//                //设置显示位置
//                //window.showAsDropDown(anchor , 0, 0);//msgView就是我们menu中的btn_msg
//                window.showAtLocation(frameLayout, Gravity.BOTTOM,0,220);
                mListPopupWindow = new ListPopupWindow(context);
                //mListPopupWindow.setAdapter(new ArrayAdapter<LocationAddressInfo>(context,R.layout.item_poi_listview,R.id.POI_title, data));
                mListPopupWindow.setAdapter(new POIList_Adapter(data,context));
                mListPopupWindow.setAnchorView(informationhint);//以哪个控件为基准，在该处以logId为基准
                mListPopupWindow.setModal(true);
                mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                        //Toast.makeText(context, "点击了"+LocationAddressInfo[position], Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
                        mListPopupWindow.dismiss();
                }
                });
                mListPopupWindow.show();
            }
        });

    }

    protected void doSearchQuery(String key) {

        currentPage = 0;
        //不输入城市名称有些地方搜索不到
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query("", key, Main_Activity.citycode);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(15);
        // 设置查询页码
        query.setPageNum(currentPage);

        //构造 PoiSearch 对象，并设置监听
        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(Main_Activity.wei, Main_Activity.jing), 1000));
        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        poiSearch.searchPOIAsyn();
    }

    protected void doRecSearchQuery(String key,int Pagesize) {
        //不输入城市名称有些地方搜索不到
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query("", key, Main_Activity.cityname);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(Pagesize);
        // 设置查询页码
        query.setPageNum(0);

        //构造 PoiSearch 对象，并设置监听
        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(Main_Activity.wei, Main_Activity.jing), 10000));
        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult Result, int rCode) {
        //rCode 为1000 时成功,其他为失败
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            // 解析result   获取搜索poi的结果
            if (Result != null && Result.getQuery() != null) {
                if (Result.getQuery().equals(query)) {  // 是否是同一条

                    if(Result.getQuery().getPageSize()==Pagesize)   //判断是否是推荐结果的部分
                    {
                        poiResult = Result;//保存推荐结果至成员变量
                        showRecOnMap();
                        return;
                    }
                    data.clear();

                    // 取得第一页的poiitem数据，页数从数字0开始
                    //poiResult.getPois()可以获取到PoiItem列表
                    List<PoiItem> poiItems = Result.getPois();

                    //若当前城市查询不到所需POI信息，可以通过result.getSearchSuggestionCitys()获取当前Poi搜索的建议城市
                    List<SuggestionCity> suggestionCities = Result.getSearchSuggestionCitys();
                    //如果搜索关键字明显为误输入，则可通过result.getSearchSuggestionKeywords()方法得到搜索关键词建议。
                    List<String> suggestionKeywords =  Result.getSearchSuggestionKeywords();

                    //解析获取到的PoiItem列表
                    for(PoiItem item : poiItems){
                        //获取经纬度对象
                        LatLonPoint llp = item.getLatLonPoint();
                        double lon = llp.getLongitude();
                        double lat = llp.getLatitude();
                        //返回POI类型描述
                        String Typedes = item.getTypeDes();
                        // 返回POI类型代码
                        String Typecode = item.getTypeCode();
                        //返回POI的名称
                        String title = item.getTitle();
                        //返回POI的地址
                        String text = item.getSnippet();
                        LocationAddressInfo l = new LocationAddressInfo(String.valueOf(lon), String.valueOf(lat), title, text,Typedes,Typecode);
                        data.add(l);
                    }
                }
            } else {
                Toast.makeText(getActivity(),"无搜索结果",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(),"错误码"+rCode,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {                //实现AmapLocationListener
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //解析定位结果
                //text_map.setText(amapLocation.getAddress());
                String tempcitycode = amapLocation.getCityCode();
                String cityname = amapLocation.getCity();
                String Adcode = amapLocation.getAdCode();
                String PoiName = amapLocation.getPoiName();


                if(!(Adcode.equals(Main_Activity.citycode))) {
                    Main_Activity.citycode = Adcode;
                    loadWeather();
                    
                }

                Main_Activity.cityname = cityname;
                Main_Activity.wei = amapLocation.getLatitude();//获取纬度
                Main_Activity.jing = amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                Main_Activity.dakatime = df.format(date);//定位时间
//                        Log.d("onLocationchanged","纬度："+Main_Activity.wei+" 经度："+Main_Activity.jing);
//                        Log.d("onLocationchanged","城市代码："+tempcitycode);
//                        Log.d("onLocationchanged","城市："+city);
//                        Log.d("onLocationchanged","Adcode："+Adcode);
//                        Log.d("onLocationchanged","PoiName："+PoiName);
//
//                        //获取当前定位结果来源，如网络定位结果，详见定位类型表
//                        Log.i("定位类型", amapLocation.getLocationType() + "");
//                        Log.i("获取纬度", amapLocation.getLatitude() + "");
//                        Log.i("获取经度", amapLocation.getLongitude() + "");
//                        Log.i("获取精度信息", amapLocation.getAccuracy() + "");
//
//                        //如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                        Log.i("地址", amapLocation.getAddress());
//                        Log.i("国家信息", amapLocation.getCountry());
//                        Log.i("省信息", amapLocation.getProvince());
//                        Log.i("城市信息", amapLocation.getCity());
//                        Log.i("城区信息", amapLocation.getDistrict());
//                        Log.i("街道信息", amapLocation.getStreet());
//                        Log.i("街道门牌号信息", amapLocation.getStreetNum());
//                        Log.i("城市编码", amapLocation.getCityCode());
//                        Log.i("地区编码", amapLocation.getAdCode());
//                        Log.i("获取当前定位点的AOI信息", amapLocation.getAoiName());
//                        Log.i("获取当前室内定位的建筑物Id", amapLocation.getBuildingId());
//                        Log.i("获取当前室内定位的楼层", amapLocation.getFloor());
//                        Log.i("获取GPS的当前状态", amapLocation.getGpsAccuracyStatus() + "");

                // 停止定位
                mLocationClient.stopLocation();
            }
        }
        getRec();
    }

    private void getRec() {
//        List<String> rectypes = this.getRectype();
//        List<String> rectypes = Arrays.asList("110000|050000|060000|070000|080000|100000|120000");
        Pagesize = 5;
//        for (int i = 0;i<rectypes.size();i++)
//        {
//            doRecSearchQuery(rectypes.get(i),Pagesize);
//        }
        //doRecSearchQuery("110000|050000|060000|070000|080000|100000|120000",Pagesize);
        Thread getrec = new Thread()
        {
            @Override
            public void run() {
                List<String> l = getRectype();
                StringBuffer param = new StringBuffer(l.get(0));
                for (int i = 1;i<l.size();i++)
                {
                    param.append("|"+l.get(i));
                }
                Log.e("FirstFragment",param.toString());
                doRecSearchQuery(param.toString(),Pagesize);
                for(Marker m : markers )
                {
                    m.destroy();
                }
            }
        };
        getrec.start();

    }

    private void showRecOnMap() {

        List<PoiItem> poiItems = poiResult.getPois();

        //解析获取到的PoiItem列表
        for(PoiItem item : poiItems){
            //获取经纬度对象
            LatLonPoint llp = item.getLatLonPoint();
            double lon = llp.getLongitude();
            double lat = llp.getLatitude();
            //返回POI类型描述
            String Typedes = item.getTypeDes();
            // 返回POI类型代码
            String Typecode = item.getTypeCode();
            //返回POI的名称
            String title = item.getTitle();
            //返回POI的地址
            String text = item.getSnippet();
            LocationAddressInfo l = new LocationAddressInfo(String.valueOf(lon), String.valueOf(lat), title, text,Typedes,Typecode);

            LatLng latLng = new LatLng(Double.parseDouble(l.getLat()),Double.parseDouble(l.getLon()));
            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(l.getTitle()).snippet(l.getTypedes()));
            markers.add(marker);
            marker.showInfoWindow();
        }
    }



    private void initLocation(){
        //声明AMapLocationClient类对象
        //初始化定位
        mLocationClient = new AMapLocationClient(context);

        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void loadWeather()
    {
        Thread citycodetest = new Thread() {
            public void run() {
                JSONObject weattherinfo = sendcitycode(Main_Activity.citycode);
                try {
                    weather = weattherinfo.getString("weather");
                    windpower = weattherinfo.getString("windpower");
                    weather_city = weattherinfo.getString("city");
                    temperature = weattherinfo.getString("temperature");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        };

        Log.e("Main_Activity.citycode ", Main_Activity.citycode);
        citycodetest.start();

    }


    @Override//我们需要写一个回调的方法,onRequestPermissionsResult,在我们需要申请权限的时候就会执行这个回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200://刚才的识别码
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//用户同意权限,执行我们的操作
                    initLocation();//开始定位
                }else{//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    Toast.makeText(getActivity(),"未开启定位权限,请手动到设置去开启权限",Toast.LENGTH_LONG).show();
                }
                break;
            default:break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Thread citycodetest = new Thread() {
            public void run() {
                sendcitycode(Main_Activity.citycode);
            }
        };
        citycodetest.start();
        try {
            citycodetest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        citycodetest.interrupt();
        //开始定位
        initLocation();
        //加载天气
        loadWeather();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }


    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(getContext());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();//启动定位
        }
    }


    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    public void loadmore() {
        // 网络请求
        String urlPath="http://www.lovecurry.club:8080/TravelApp/news/DoNews";
        URL url;
        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200

                //读取返回的json数据
                InputStream inputStream=conn.getInputStream();
                // 调用自己写的NetUtils() 将流转成string类型
                String json= NetUtils.readString(inputStream);
                //System.out.println(json);
                //System.out.println("Main_Activity json:"+json);
                inputStream.close();
                JSONArray newsarray = new JSONArray(json);

                System.out.println(newsarray.length()+"条news");

                for (int i=0;i<newsarray.length();i++){
                    String time = newsarray.getJSONObject(i).getString("time");
                    String description = newsarray.getJSONObject(i).getString("descrption");
                    String title = newsarray.getJSONObject(i).getString("title");
                    String picUrl = newsarray.getJSONObject(i).getString("picUrl");
                    String passageurl = newsarray.getJSONObject(i).getString("url");

                    String utf8_title = new String(title.getBytes(),"utf-8");
                    String utf8_picurl = new String(picUrl.getBytes(),"utf-8");
                    String utf8_time = new String(time.getBytes(),"utf-8");
                    String utf8_url = new String(passageurl.getBytes(), "utf-8");
                    utf8_time = utf8_time.substring(0,utf8_time.length()-8);

                    title_list.add(utf8_title);
                    picurl_list.add(utf8_picurl);
                    time_list.add(utf8_time);
                    web_list.add(utf8_url);
                }
//                Gson gson=new Gson();  //引用谷歌的json包
//                User user=gson.fromJson(json,User.class); //谷歌的解析json的方法
//
//                int userID;
//                User user = new User(userID,name,pwd);
//
//                id =user.getId();  //然后user.get你想要的值
//                String username=user.getUsername();
//                String password=user.getPassword();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject sendcitycode(String citycode) {
        // 网络请求
        String urlPath="http://www.lovecurry.club:8080/TravelApp/location/DoWeather";
        String content = "cityCode="+citycode;
        URL url;
        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");

            OutputStream os=conn.getOutputStream();
            os.write(content.getBytes()); //字符串写进二进流
            os.flush();
            os.close();

            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200

                //读取返回的json数据
                InputStream inputStream=conn.getInputStream();
                // 调用自己写的NetUtils() 将流转成string类型
                String json= NetUtils.readString(inputStream);
                //System.out.println(json);
                System.out.println("Main_Activity json:"+json);

                JSONObject weattherinfo = new JSONObject(json);
//                String weather = weattherinfo.getString("weather");
//                String windpower = weattherinfo.getString("windpower");
//                String city = weattherinfo.getString("city");
//                String temperature = weattherinfo.getString("temperature");
//
//                weatherView.setText(city+" : "+weather);
//                temperatureView.setText("温度："+temperature);
//                windpowerView.setText("风力"+windpower);
                return weattherinfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private List<String> getRectype() {
        ArrayList<String> rectype = new ArrayList<String>();
        String urlPath="http://www.lovecurry.club:8080/TravelApp/location/getRecommend";
        URL url;
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());

            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200

                //读取返回的json数据
                InputStream inputStream=conn.getInputStream();
                // 调用自己写的NetUtils() 将流转成string类型
                String src= NetUtils.readString(inputStream);
                //System.out.println(json);

                String pattern = "\\'\\d{6}\\'";
                Pattern p = Pattern.compile(pattern);

                Matcher matcher = p.matcher(src);

                List<String> matchStrs = new ArrayList<>();
                while(matcher.find())
                {

                    matchStrs.add(matcher.group().replaceAll("\\'",""));

                    System.out.println(matcher.group());
                }

                return matchStrs;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public void GetWeb(String message,String title)
    {
        Intent it = new Intent(getActivity(), News_Activity.class);
        it.putExtra(EXTRA_MESSAGE, message);
        it.putExtra(TITLE,title);
        startActivity(it);
    }

}
