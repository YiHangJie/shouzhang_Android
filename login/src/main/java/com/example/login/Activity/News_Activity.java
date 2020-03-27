package com.example.login.Activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.Activity.Main_Activity;
import com.example.login.NetUtils;
import com.example.login.R;
import com.example.login.websocket_Manager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class News_Activity extends AppCompatActivity {

    private String url;
    private String title;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.news_webview);
        Intent intent = getIntent();
        url = intent.getStringExtra(Main_Activity.EXTRA_MESSAGE);
        title = intent.getStringExtra(Main_Activity.TITLE);

        init();

    }

    private void init() {
        webView = (WebView) findViewById(R.id.news_webview);
        //需要加载的网页的url
        webView.loadUrl(url);
        WebSettings settings = webView.getSettings();
        // 如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //等待证书响应
                handler.proceed();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final String uname_close = websocket_Manager.getUsername();
        Log.e("News_activity","onDestroy()");
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
}
