package com.example.login.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.login.Activity.Main_Activity;
import com.example.login.JSESSIONID;
import com.example.login.JWebSocketClient;
import com.example.login.LocationAddressInfo;
import com.example.login.R;
import com.example.login.websocket_Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Fragment_setRecord extends Fragment implements PoiSearch.OnPoiSearchListener{

    public static Context mContext;
    private WebView mWebView;

    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch       poiSearch;// POI搜索
    private TextView informationhint;
    public static LinkedList<LocationAddressInfo> data = new LinkedList<LocationAddressInfo>();//自己创建的数据集合

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("Fragment_setRecord","----onAttach()----");
        mContext = getActivity();

                    doRecSearchQuery("050000|060000|080000|100000|110000|140000",0);
                    doRecSearchQuery("050000|060000|080000|100000|110000|140000",1);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment_setRecord","----onDetach()----");
        data.clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_setrecord, container, false);

        mWebView = view.findViewById(R.id.js_setRecord);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 设置WebView属性
        WebSettings settings = mWebView.getSettings();
        //支持js
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

//        mWebView.addJavascriptInterface(new AndroidtoJs(mContext),"android_setRecord");

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                // 拦截输入框(原理同方式2)
                // 参数message:代表promt（）的内容（不是url）
                // 参数result:代表输入框的返回值
                    // 根据协议的参数，判断是否是所需要的url(原理同方式2)
                    // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                    //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                    Uri uri = Uri.parse(message);
                    // 如果url的协议 = 预先约定的 js 协议
                    // 就解析往下解析参数
                    if ( uri.getScheme().equals("js")) {

                        // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                        // 所以拦截url,下面JS开始调用Android需要的方法
                        if (uri.getAuthority().equals("webview")) {

                            switch (uri.getQuery()){
                                case "arg=getRecLocData":{

                                    // 执行JS所需要调用的逻辑
                                    System.out.println("js调用了Android的方法");
                                    // 可以在协议上带有参数并传递到Android上
//                                    HashMap<String, String> params = new HashMap<>();
//                                    Set<String> collection = uri.getQueryParameterNames();

                                    String res = getRecLocData();
                                    Log.d("Fragment_setRecord",res);
                                    //参数result:代表消息框的返回值(输入值)
                                    result.confirm(res);
                                    break;
                                }
                                case "arg=getSession":
                                    JSONObject session = new JSONObject();
                                    try {
                                        session.put("session", JSESSIONID.getJSESSIONIDNAME());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    result.confirm(session.toString());
                                    break;
                                case "arg=getUname":{
                                    String uname = websocket_Manager.getUsername();
                                    result.confirm(uname);
                                }
                            }
                        }
                        return true;
                    }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

       mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //等待证书响应
                handler.proceed();
            }

//           @Override
//           public void onPageStarted(WebView view, String url, Bitmap favicon) {
//               super.onPageStarted(view, url, favicon);
//               mWebView.loadUrl("javascript:callAndroid()");
//           }
       });
        mWebView.loadUrl("file:///android_asset/questionaire.html");
    }

    private String getRecLocData() {
        String result;
        JSONObject layer1 = new JSONObject();
        for(int i=0;i< data.size();i++)
        {
            JSONObject layer2 = new JSONObject();
            try {
                layer2.put("longitude",data.get(i).getLon());
                layer2.put("latitude",data.get(i).getLat());
                layer2.put("title",data.get(i).getTitle());
                layer2.put("address",data.get(i).getAddress());
                layer2.put("typedes",data.get(i).getTypedes());
                layer2.put("typecode",data.get(i).getTypecode());
                layer1.put("loc"+i,layer2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        result = layer1.toString();
        Log.d("AndroidtoJs",result);
        return result;
    }

    protected void doRecSearchQuery(String key,int currentPage) {

        //不输入城市名称有些地方搜索不到
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query("", key, Main_Activity.cityname);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(24);
        // 设置查询页码
        query.setPageNum(currentPage);

        //构造 PoiSearch 对象，并设置监听
        poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(this);
        //poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(Main_Activity.wei, Main_Activity.jing), 10000));
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
                    poiResult = Result;

                    // 取得第一页的poiitem数据，页数从数字0开始
                    //poiResult.getPois()可以获取到PoiItem列表
                    List<PoiItem> poiItems = poiResult.getPois();

                    //若当前城市查询不到所需POI信息，可以通过result.getSearchSuggestionCitys()获取当前Poi搜索的建议城市
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();
                    //如果搜索关键字明显为误输入，则可通过result.getSearchSuggestionKeywords()方法得到搜索关键词建议。
                    List<String> suggestionKeywords =  poiResult.getSearchSuggestionKeywords();

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
                        data.add(new LocationAddressInfo(String.valueOf(lon), String.valueOf(lat), title, text,Typedes,Typecode));
                        Log.d("Fragment_setRecord","关键字搜索结果(设置边界）："+"类别："+Typedes+"    名称"+title+"    地址："+text);
                    }
                }
            } else {
                Toast.makeText(mContext,"无搜索结果",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext,"错误码"+rCode,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

}
