package com.example.administrator.myapplication;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    com.tencent.smtt.sdk.WebView webview;
    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webview=findViewById(R.id.webview);
        initWebView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        webview.destroy();
        webview=null;
    }
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗

        @Override
        public boolean onJsAlert(com.tencent.smtt.sdk.WebView webView, String s, String s1, JsResult jsResult) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(s1).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            jsResult.confirm();
            return true;
        }

        @Override
        public void onReceivedTitle(com.tencent.smtt.sdk.WebView webView, String s) {
            Log.d("titile","======="+s);
            super.onReceivedTitle(webView, s);
        }

    };
    private void initWebView(){
        webview.getSettings().setTextZoom(100);//当前字体百分比
        webview.getSettings().setDefaultTextEncodingName("utf-8");//指定编码方式
        webview.getSettings().setJavaScriptEnabled(true);//支持js
        webview.getSettings().setDomStorageEnabled(true); //设置支持DomStorage
        webview.getSettings().setAllowFileAccess(true);//设置在WebView内部是否允许访问文件
        webview.getSettings().setBlockNetworkLoads(false);//设置WebView是否从网络加载资源，Application需要设置访问网络权限，否则报异常
        webview.getSettings().setBlockNetworkImage(false);//设置WebView是否以http、https方式访问从网络加载图片资源，默认false
        webview.loadUrl("https://zyzcss.github.io/bootstrapTest/others/musicPlayer.html");
        // 给WebView设置监听
        webview.setWebViewClient(new WebViewClient() {
            //跳转连接
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                // 所有连接强制在当前WeiView加载，不跳服务器
                webview.loadUrl(url);
                return true;
            }
            //加载结束
            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                super.onPageFinished(view, url);
                //webview.loadUrl("javascript:openMusic()");//加载结束，加载音乐
            }
        });
        webview.setWebChromeClient(webChromeClient);
    }

}
