package com.example.administrator.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/6/5.
 */

public class FxService extends Service {
    //定义浮动窗口布局
    LinearLayout mFloatLayout=null;
    WindowManager.LayoutParams params;
    //创建浮动窗口设置布局参数的对象
    WindowManager windowManager;
    TextView name=null,singer=null,start=null,end=null,corver=null,player_type=null;
    SeekBar seekBar;
    boolean isPause;
    Button pause,pre,next;
    private GestureDetector mGestureDetector;
    Intent _intent;
    private MyBinder binder = new MyBinder();
    @Override
    public void onCreate()
    {
        super.onCreate();
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d("bing","绑定成功");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setInfo(intent);
        return super.onStartCommand(intent, flags, startId);
    }
    public void setInfo(Intent intent){
        Log.d("启动","悬浮窗数据开始");
        params.width= windowManager.getDefaultDisplay().getWidth();
        name.setText(intent.getStringExtra("name"));
        singer.setText(intent.getStringExtra("singer"));
        end.setText(timeTotime(intent.getIntExtra("end",0)));
        isPause=intent.getBooleanExtra("isPause",true);
        Log.d("isPause",isPause+"");
        if(isPause){
            pause.setBackgroundResource(R.drawable.pause);
        }else {
            pause.setBackgroundResource(R.drawable.start);
        }
        Log.d("启动","悬浮窗数据完毕");
        windowManager.updateViewLayout(mFloatLayout,params);
    }
    private void createFloatView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
       /* params.gravity = Gravity.LEFT | Gravity.TOP;*/
        params.flags = LayoutParams.FLAG_NOT_FOCUSABLE;//FLAG_NOT_TOUCH_MODAL;
        params.x = 300;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.activity_player, null).findViewById(R.id.activity_player);
        params.width= 0;
        params.height = LayoutParams.MATCH_PARENT;

        windowManager.addView(mFloatLayout, params);
        seekBar=mFloatLayout.findViewById(R.id.playSeekBar);

        player_type=mFloatLayout.findViewById(R.id.player_type);
        player_type.setAlpha(0.4f);
        name=mFloatLayout.findViewById(R.id.player_name);
        singer=mFloatLayout.findViewById(R.id.player_singer);
        start=mFloatLayout.findViewById(R.id.player_time_start);
        end=mFloatLayout.findViewById(R.id.player_time_end);
        corver=mFloatLayout.findViewById(R.id.player_corver);
        Button b1=mFloatLayout.findViewById(R.id.playerBack);
        _intent=new Intent("com.example.communication.RECEIVER");
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            _intent.putExtra("isMini", true);
            _intent.putExtra("changeP", false);
            sendBroadcast(_intent);
            params.width= 0;
            windowManager.updateViewLayout(mFloatLayout,params);
            }
        });
        pre=mFloatLayout.findViewById(R.id.player_pre);

        pause=mFloatLayout.findViewById(R.id.player_pause);

        next=mFloatLayout.findViewById(R.id.player_next);
        Log.d("启动","加载悬浮窗完毕");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("卸载","卸载service");
        windowManager.removeView(mFloatLayout);
    }
    public String timeTotime(int time){
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        return sd;
    }
    public class MyBinder extends Binder {

        public FxService getService(){
            return FxService.this;
        }
    }
}
