package com.example.administrator.myapplication.net;

/**
 * Created by Administrator on 2018/6/6.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import id.zelory.compressor.Compressor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ImageTask extends AsyncTask<Object,Void, Bitmap> {
    CallBack callBack;

    public ImageTask(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected  Bitmap doInBackground(Object... params) {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url((String) params[0]).build();
        Bitmap bitmap=null;
        //Bitmap pic=null;
        byte[] temp=null;
        try {
            Response response=client.newCall(request).execute();
            temp=response.body().bytes();
            byte[] buffer = new byte[1024];
            File tempFile = File.createTempFile("temp"+ new Date().getTime(), ".bmp");//创建临时文件
            //String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zyzcss/temp/";
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            try {
                fileOutputStream.write(temp);
                fileOutputStream.flush();
                bitmap= new Compressor((Context) params[1])
                        .setMaxWidth(300)
                        .setMaxHeight(300)
                        .setQuality(15)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToBitmap(tempFile);
            } finally {
                fileOutputStream.close();
                tempFile.deleteOnExit();//程序退出时删除临时文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute( Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(callBack!=null){
            callBack.getData(bitmap);
        }
    }

    public interface CallBack{
        void getData( Bitmap temp);
    }
}
