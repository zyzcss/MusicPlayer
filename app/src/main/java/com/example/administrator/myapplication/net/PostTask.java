package com.example.administrator.myapplication.net;

/**
 * Created by Administrator on 2018/6/10.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostTask extends AsyncTask<String,Void,String> {
    CallBack callBack;

    public PostTask(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected String doInBackground(String... params) {
        String result=null;
        //实例化
        FormBody formBody = new FormBody
                .Builder()
                .add("s",params[1])
                .add("type","1000")
                .add("limit","15")
                .add("offset",params[2])//设置参数名称和参数值
                .build();
        OkHttpClient client=new OkHttpClient();
        //创建一个request对象用于发送http请求
        Request request=new Request.Builder()
                .url(params[0]).post(formBody).build();
        try {
            //发送请求 获取服务器返回数据
            Response response=client.newCall(request).execute();
            result=response.body().string();
            Log.d("获得服务器返回信息",result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(callBack!=null){
            callBack.getData(result);
        }
    }

    public interface CallBack{
        void getData(String result);
    }
}
