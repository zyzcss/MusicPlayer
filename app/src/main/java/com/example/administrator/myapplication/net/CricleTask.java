package com.example.administrator.myapplication.net;

/**
 * Created by Administrator on 2018/6/6.
 */


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;


public class CricleTask extends AsyncTask<Bitmap,Void,Bitmap> {
    CallBack callBack;

    public CricleTask(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        //byte[] temp=params[0];
        return createCircleImage(params[0]);
    }
    public Bitmap createCircleImage(Bitmap source) {
        int length = source.getWidth() < source.getHeight() ? source.getWidth() : source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(length / 2, length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(callBack!=null){
            callBack.getData(bitmap);
        }
    }

    public interface CallBack{
        void getData(Bitmap temp);
    }
}
