package com.example.administrator.myapplication;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.LongDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.myapplication.bean.Music;
import com.example.administrator.myapplication.bean.Track;
import com.example.administrator.myapplication.net.CricleTask;
import com.example.administrator.myapplication.net.ImageTask;
import com.example.administrator.myapplication.tookit.JSONParser;
import com.example.administrator.myapplication.net.JSONTask;
import com.example.administrator.myapplication.tookit.MusicAdapter;
import com.example.administrator.myapplication.tookit.MusicSqlLite;
import com.example.administrator.myapplication.net.PostTask;
import com.example.administrator.myapplication.tookit.SearchAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class MusiclistActivity extends AppCompatActivity{
    ListView listView;
    View footView;
    ArrayList<Music> data;
    ArrayList<Track> searchs;
    MusicAdapter adapter;
    AlertDialog.Builder builder;
    SearchAdapter searchAdapter;
    Dialog dialog=null;
    Intent intentService=null,intentReturn,intentSong=null;
    TextView name, singer,corver,search_input;
    boolean
            isDown, isLoad = true, isMini = true,isPause=true,isLoading=false,corverStop=true,isSearch=false,
            isList=true,needLoad=false,isLong=false;
    int len = 0, current = 0,seekbar=0,playType=0,searchOffset=0,nowDel=-1,corverslider=0;
    final int[] playTypes=new int[]{R.drawable.loop_next,R.drawable.loop_rand,R.drawable.loop_me};
    MsgReceiver msgReceiver;
    MusicReceiver musicReceiver;
    Button pause,list_change;
    Matrix matrix=null;
    private FxService bindService = null;
    private MyService control=null;
    //private GestureDetector mGestureDetector;
    float deg=0;
    float pox=1000.0f;
    Bitmap bitmap1,bitmap2;
    Canvas canvas;
    String searchContent="",url="";
    Paint paint;
    MusicSqlLite helper;
    SQLiteDatabase db;
    SharedPreferences spf;
    //悬浮窗的连接
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FxService.MyBinder binder = (FxService.MyBinder) service;
            bindService = binder.getService();
            Log.d("接口","获得返回的参数=======");
            bindService.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Log.d("isPause",isPause+"");
                playerClick();
                }
            });
            bindService.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    //control.musicSeekTo(seekBar2.getProgress());
                    Log.d("seekbar",seekBar2.getProgress()+"---------------------");
                    control.pauseProgress();
                    bindService.seekBar.setProgress(seekBar2.getProgress());
                    bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
                    control.musicSeekTo(seekBar2.getProgress());
                    control.updateProgress();

                }
            });
            bindService.pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // pox=1000.0f;
                    preOrNext(true);
                   // slideToRight(bindService.corver);
                }
            });
            bindService.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preOrNext(false);
                    //bindService.corver.clearAnimation();
                   // pox=-1000.0f;
                   // slideToRight(bindService.corver);
                }
            });
            bindService.player_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(playType<2){
                        playType++;
                    }else{
                        playType=0;
                    }
                    bindService.player_type.setBackgroundResource(playTypes[playType]);
                }
            });
            //封面手势
            bindService.corver.setOnTouchListener(new View.OnTouchListener() {
                int lastX,lastY;
                boolean isTouch=false;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("手势","===========");
                    int ea = event.getAction();
                    switch (ea) {
                        case MotionEvent.ACTION_UP:
                            if(isTouch){
                                isTouch=false;
                                lastX = (int) event.getRawX()-lastX;//获取触摸事件触摸位置的原始X坐标
                                lastY = (int) event.getRawY()-lastY;
                                Log.d("手势","起来"+lastX+","+lastY);
                                if(lastX>350&&lastY<300){
                                    preOrNext(true);
                                    pox=1000.0f;
                                    slideToRight(bindService.corver);
                                }
                                if(lastX<-350&&lastY<300){
                                    preOrNext(false);
                                    pox=-1000.0f;
                                    slideToRight(bindService.corver);
                                }
                                if(Math.abs(lastX)<20&&Math.abs(lastY)<20){
                                    Log.d("点击","点击封面");
                                    playerClick();
                                }

                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                            lastY = (int) event.getRawY();
                            Log.d("手势","按下"+lastX+","+lastY);
                            isTouch=true;
                            break;
                    }
                    return true;
                }

            });
            //rotateStart(0.0f);
        }
        //client 和service连接意外丢失时，会调用该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("连接意外","onServiceDisconnected  A");
        }
    };
    //悬浮窗的暂停开始
    private void playerClick(){
        Log.d("isPause",isPause+"");
        if(isPause){
            bindService.pause.setBackgroundResource(R.drawable.pause);
        }else{
            bindService.pause.setBackgroundResource(R.drawable.start);
        }
        try {
            changeState(pause);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
    }
    //上一首下一首
    public void preOrNext(boolean isPre){
        Log.d("切歌",current+"");
        switch (playType){
            case 0:
                //顺序播放
                Log.d("顺序播放","顺序播放");
                if(isPre){
                    current --;
                    if (current<0){
                        current=data.size()-1;
                    }
                    Log.d("上一首",current+"");
                }else{
                    current++;
                    if (current>data.size()-1){
                        current=0;
                    }
                    Log.d("下一首",current+"");
                }
                break;
            case 1:
                //随机播放
                Random random=new Random();
                current= random.nextInt(data.size());
                break;
            case 2:
                //单曲循环
                break;
        }
        changeSong(current,true);
    }
    //音乐的连接
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("音乐","绑定成功2");
            MyService.MusicControl binder = (MyService.MusicControl) service;
            control = (MyService) binder.getService();
            control.url="";
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("音乐","绑定失败");
        }
    };
    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);
        spf= PreferenceManager.getDefaultSharedPreferences(this);
        //广播监听器 注册
        msgReceiver = new MsgReceiver();
        musicReceiver=new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
        //广播监听器 注册
        IntentFilter intentFilter2=new IntentFilter();
        intentFilter2.addAction("com.seekbar");
        registerReceiver(musicReceiver,intentFilter2);
        intentService = new Intent(MusiclistActivity.this, FxService.class);
        intentService.setAction("android.intent.action.RESPOND_VIA_MESSAGE");

        intentSong=new Intent(MusiclistActivity.this,MyService.class);
        Log.d("绑定","绑定view1");
        bindService(intentSong,connection, Context.BIND_AUTO_CREATE);
        Log.d("绑定","绑定view2");
        bindService(intentService,conn,BIND_AUTO_CREATE);
        helper=new MusicSqlLite(this,"mydb.dbs",null,1);
        db=helper.getWritableDatabase();
        intentReturn=new Intent("com.example.communication.RETURN");
        pause=(Button)  findViewById(R.id.list_pause);
        listView = (ListView) findViewById(R.id.list);
        corver=(TextView)findViewById(R.id.mini_corver);
        name = (TextView) findViewById(R.id.mini_name);
        singer = (TextView) findViewById(R.id.mini_singer);
        footView = LayoutInflater.from(this).inflate(R.layout.footerview, null);
        search_input=(TextView)findViewById(R.id.search_input);
        list_change=(Button)findViewById(R.id.list_change);
        data = new ArrayList<>();
        searchs=new ArrayList<>();
        adapter = new MusicAdapter(this, R.layout.music_item, data);
        searchAdapter = new SearchAdapter(this, R.layout.music_item, searchs);
        url=spf.getString("url","2029258023")!=""?spf.getString("url","2029258023"):"2029258023";
        Log.d("url",url);
        listView.addFooterView(footView);
        listView.setAdapter(adapter);
        //滑动
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isDown == true && scrollState == SCROLL_STATE_IDLE) {
                    if(isList){
                        Log.d("加载","加载歌");
                        //loadData(false);
                        listDown("已经到底啦");
                    }else{
                        Log.d("加载","加载搜素");
                        loadSearch();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    isDown = true;
                } else {
                    isDown = false;
                }
            }
        });
        //歌曲长按
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("长按","=============");
                isLong=true;
                nowDel=position;
                onDialog();
                return false;
            }
        });
        //歌曲单击
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("点击","position"+position);
                if(isList){
                    if(position<data.size()&&!isLong) {
                        Log.d("歌曲", "切换歌曲");
                        if (data.size() > 0) changeSong(position,true);
                    }
                }else{
                    if(position<searchs.size()){
                        Log.d("搜索","搜索列表");
                        url=searchs.get(position).getId()>0?searchs.get(position).getId()+"":"";
                        Log.d("url",url);
                        changeSongList();
                    }
                }
            }
        });
/*        //手势
        mGestureDetector = new GestureDetector(new gestureListener());
        listView.setOnTouchListener(this);*/
        //混合选项 制作corver动画
        matrix= new Matrix();
        //设置搜索按钮匹配
        search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d("搜索","===="+actionId+","+EditorInfo.IME_ACTION_SEARCH);
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("搜索","搜索回车按钮匹配");
                searchContent=search_input.getText().toString();
                searchOffset=0;
                //isList=false;
                //list_change.setBackgroundResource(R.drawable.list1);
                searchs.clear();
                searchAdapter.notifyDataSetChanged();
                loadSearch();
            }
            return false;
            }
        });
        Cursor cursor = db.query("music",null,null,null,null,null,"ids asc");
        Log.d("dbs",cursor.getColumnCount()+"");
        if(cursor.getCount()>1){
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name2 = cursor.getString(cursor.getColumnIndex("name"));
                String artists = cursor.getString(cursor.getColumnIndex("artists"));
                String cover = cursor.getString(cursor.getColumnIndex("cover"));
                String album = cursor.getString(cursor.getColumnIndex("album"));
                int time = cursor.getInt(cursor.getColumnIndex("time"));
                data.add(new Music(id,name2,artists,album,cover,time));
                cursor.moveToNext();
                Log.d("datas",data.get(i).getName());
            }
            name.setText(data.get(0).getName() + "");
            singer.setText(data.get(0).getArtists());
            //setImg(data.get(0));
        }else{
            needLoad=true;
            loadData(false);

        }
        setImg(data.get(0));
    }
    public void onDialog()
    {
        if(dialog!=null){
            dialog.cancel();
        }
        dialog=null;
        dialog=new Dialog(this);//可以在style中设定dialog的样式
        dialog.setContentView(R.layout.dialog);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        //设置该属性，dialog可以铺满屏幕
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                nowDel=-1;
                Log.d("取消","========");
                isLong=false;
            }
        });
//      dialog.getWindow().setWindowAnimations();
        slideToUp(dialog.getWindow().findViewById(R.id.layout));
    }
    //删除歌曲时弹窗
    public void delSong(View view){
        Log.d("删除",current+"."+nowDel);
        if(nowDel<data.size()){
            control.musicStop();
            data.remove(nowDel);
            if(current>nowDel){
                current-=1;
            }else if(current==nowDel){
                changeSong(current,true);
            }
        }
        /*if(nowDel<data.size() && nowDel!=current && !isPause){
            data.remove(nowDel);
        }else{
            control.musicStop();
            data.remove(nowDel);
            current-=1;
        }*/
        adapter.notifyDataSetChanged();
        dialog.cancel();
    }
    //取消dialog
    public void cancelDialog(View view){
        dialog.cancel();
    }
    //弹窗 从下往上弹
    public static void slideToUp(View view){
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(300);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    //封面动画 步骤1
    public void slideToRight(View view){
        if(corverslider==0){
            corverslider+=1;
            Animation slide = new TranslateAnimation(0.0f, pox, 0.0f, 0.0f);
            slide.setDuration(600);
            slide.setFillAfter(true);
            slide.setFillEnabled(true);
            view.startAnimation(slide);
            slide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(corverslider==1){
                        slideStart(-pox);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
    //封面动画 步骤2
    public void slideStart(float pox){
        Animation slide = new TranslateAnimation(pox,0.0f , 0.0f, 0.0f);
        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        bindService.corver.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                corverslider=0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    //刷新歌曲信息
    public void changeSong(int position,boolean ischangeImg){
        Log.d("点击", "点击");
        current = position;
        name.setText(data.get(current).getName() + "");
        singer.setText(data.get(current).getArtists());
        control.url=data.get(current).getId()+"";
        control.stopCorver();
        control.musicStop();
        Log.d("点击", "点击=============");
        seekbar=0;
        isPause=true;
        corverStop=false;
        bindService.name.setText(data.get(current).getName());
        bindService.singer.setText(data.get(current).getArtists());
        bindService.end.setText(timeTotime(data.get(current).getTime()));
        bindService.isPause=isPause;
        if(ischangeImg)setImg(data.get(current));
        playerClick();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除广播
        Log.d("=================","===================");
        SharedPreferences.Editor editor;
        if(musicReceiver!=null)unregisterReceiver(musicReceiver);
        if(msgReceiver!=null)unregisterReceiver(msgReceiver);
        if(conn!=null)unbindService(conn);
        if(connection!=null)unbindService(connection);
        db.execSQL("delete from music");
        for(int i=0;i<data.size();i++){
            db.execSQL("insert into music (ids,id,name,album,cover,time,artists) values(?,?,?,?,?,?,?)",
                    new Object[]{i,data.get(i).getId(),data.get(i).getName(),data.get(i).getAlbum(),data.get(i).getCover(),data.get(i).getTime(),data.get(i).getArtists()});
            Log.d("datas",data.get(i).getName());
        }
        db.close();
        Log.d("url",url);
        editor=spf.edit();
        editor.putString("url",url);
        editor.apply();
        editor.commit();
    }
    //监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("isMini", isMini + "");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isMini) {
                finish();
                return super.onKeyDown(keyCode, event);
            } else {
                isMini=true;
                bindService.params.width= 0;
                bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    //加载数据
    public void loadData(final Boolean play) {
        listDown();
        if (isLoad && needLoad) {
            isLoad = false;
            Log.d("进1","===============");
            new JSONTask(new JSONTask.CallBack() {
                @Override
                public void getData(String result) {
                    Log.d("len", result);
                    ArrayList<Music> temp = JSONParser.MyJsonParer(result); //存放解析好的json数据
                    data.addAll(temp);
                    if(current<data.size()){
                        name.setText(data.get(current).getName() + "");
                        singer.setText(data.get(current).getArtists());
                    }
                    adapter.notifyDataSetChanged();
                    isLoad = true;
                    changeToSong();
                    //setImg(data.get(current));
                    if(play&&data.size()>0)changeSong(0,true);
                    needLoad=false;
                }
            }).execute("http://music.163.com/api/playlist/detail?id="+url+"&updateTime=-1");
        }else{
            listDown("已经到底啦");
        }
    }
    //底部视图更新
    private void listDown(String str){
        Log.d("加载","隐藏");
        GifImageView imageView = (GifImageView) footView.findViewById(R.id.imageView3);
        imageView.setVisibility(View.GONE);
        ((TextView) footView.findViewById(R.id.footer)).setText(str);
        adapter.notifyDataSetChanged();
    }
    private void listDown(){
        Log.d("加载","显示");
        GifImageView imageView = (GifImageView) footView.findViewById(R.id.imageView3);
        imageView.setVisibility(View.VISIBLE);
        ((TextView) footView.findViewById(R.id.footer)).setText("");
        adapter.notifyDataSetChanged();
    }
    //悬浮窗启动 layout
    public void getMenu(View v) {
        getMenuStart(true);
    }
    //悬浮窗启动 手动
    public void getMenuStart(boolean isStart ){
        Log.d("getMenuStart","================");
        if(intentService!=null)stopService(intentService);
        setImg(data.get(current));
        if(!corverStop){
            Log.d("封面旋转",isPause+"==========");
            control.stopCorver();
            control.updateCorver();
            bindService.name.setText(data.get(current).getName());
            bindService.singer.setText(data.get(current).getArtists());
            bindService.isPause=isPause;
            bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
            corverStop=true;
        }
        if(isStart){
            isMini = false;
            corverStop=true;
            if(isPause){
                control.stopCorver();
            }else{
                startRotateAni(control.deg);
            }
            Log.d("启动", "显示悬浮窗1");
            intentService.putExtra("isPause", !isPause);
            intentService.putExtra("name", data.get(current).getName());
            intentService.putExtra("singer", data.get(current).getArtists());
            intentService.putExtra("end", data.get(current).getTime());
            startService(intentService);
        }
    }
    //图片加载及设置
    public void setImg(Music music){
        if(music.getCorverImg()==null){
            if(!isLoading){
                isLoading=true;
                new ImageTask(new ImageTask.CallBack() {
                    @Override
                    public void getData( Bitmap bitmap) {
                        Log.d("判断图片1","加载");
                        data.get(current).setBackImg(bitmap);
                        corver.setBackground(new BitmapDrawable(data.get(current).getBackImg()));
                        new CricleTask(new CricleTask.CallBack() {
                            @Override
                            public void getData(Bitmap temp) {
                                Log.d("图片","处理圆OK");
                                data.get(current).setCorverImg(temp);
                                bindService.mFloatLayout.findViewById(R.id.player_corver).setBackground(new BitmapDrawable(temp));
                                bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
                                isLoading=false;
                            }
                        }).execute(data.get(current).getBackImg());
                    }
                }).execute(data.get(current).getCover(),MusiclistActivity.this);
            }
        }else{
            Log.d("判断图片","已经存在");
            bindService.mFloatLayout.findViewById(R.id.player_corver).setBackground(new BitmapDrawable(data.get(current).getCorverImg()));
            bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
            corver.setBackground(new BitmapDrawable(data.get(current).getBackImg()));
            bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
        }
    }
    //广播接收器
    public class MsgReceiver extends BroadcastReceiver {
        public MsgReceiver() {}
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("广播","收到广播消息（悬浮窗返回）");
            isMini = true;
            control.pauseCorver();
            if(!isPause)corverStop=false;
            if(intent.getBooleanExtra("changeBar",false)){
                Log.d("changeBar","changeBar");
                seekbar=intent.getIntExtra("seekBar",0);
                control.musicSeekTo(seekbar);
            }
        }
    }
    //广播接收器---music
    public class  MusicReceiver extends BroadcastReceiver{
        public MusicReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",-1);
            switch (type){
                case 1:
                    deg=intent.getFloatExtra("deg",0);
                    startRotateAni(deg);
                    break;
                case 2:
                    seekbar=intent.getIntExtra("current",0);
                    Log.d("广播","收到广播消息（music返回）"+seekbar);
                    if(data.size()>0) bindService.seekBar.setMax(data.get(current).getTime());
                    bindService.seekBar.setProgress(seekbar);
                    bindService.start.setText(timeTotime(seekbar));
                    break;
            }
            bindService.windowManager.updateViewLayout(bindService.mFloatLayout,bindService.params);
        }
    }
    //时间转换器
    public String timeTotime(int time){
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
        return sdf.format(new Date(Long.parseLong(String.valueOf(time))));
    }
    //暂停开始状态改变
    public void changeState(View view) throws IOException {
        if(control.url=="")control.url=data.get(current).getId()+"";
        Log.d("时间---changeState",seekbar+"");
        if(isPause){
            view.setBackgroundResource(R.drawable.pause);
            Log.d("url--control",control.url);
            control.musicPlay(seekbar);
            control.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    control.musicStop();
                    preOrNext(false);
                   // changeSong(current);
                }
            });
            control.player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                   // Toast.makeText(MusiclistActivity.this,"该歌曲版权问题无法听",Toast.LENGTH_LONG);
                    //alert("错误","该歌曲版权问题无法听","确认");
                    bindService.corver.clearAnimation();
                    control.musicStop();
                    Log.d("音乐","错误"+current);
                    data.remove(current);
                    if(current>0)current-=1;
                    adapter.notifyDataSetChanged();
                    try
                    {
                        Thread.currentThread().sleep(1000);
                        preOrNext(false);
                       // changeSong(current);
                    }
                    catch(Exception e){

                    }
                    return false;
                }
            });
        }else {
            control.musicPause();
            view.setBackgroundResource(R.drawable.start);
        }
        isPause=!isPause;
    }
    //封面动画
    private void startRotateAni(float deg){
        if(data.size()>0&&data.get(current).getCorverImg()!=null){
            bitmap1=data.get(current).getCorverImg();
            matrix.setRotate(deg,bitmap1.getWidth()/2,bitmap1.getHeight()/2);
            bitmap2 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
            canvas = new Canvas(bitmap2);
            paint = new Paint();
            canvas.drawBitmap(bitmap1, matrix, paint);
            bindService.corver.setBackground(new BitmapDrawable(bitmap2));
            bitmap1=null;
            bitmap2=null;
            canvas=null;
            paint=null;
        }
    }
/*    //手势
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    //手势
    public class gestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("duan安","==========");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("手势","=========");
            if(isMini){
                if(e2.getX()-e1.getX()<TOUCH_MIN&&e2.getY()-e1.getY()<150){
                    getMenuStart(true);
                    bindService.params.x=100;
                }
            }else{
                if(e2.getX()-e1.getX()<TOUCH_MIN&&e2.getY()-e1.getY()<150){
                    preOrNext(false);
                }
                if(e2.getX()-e1.getX()>-TOUCH_MIN&&e2.getY()-e1.getY()<150){
                    preOrNext(true);
                }
            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("长安","==========");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }*/
    //搜索按钮点击
    public void search(View v){
        searchContent=search_input.getText().toString();
        searchOffset=0;
        searchs.clear();
        searchAdapter.notifyDataSetChanged();
        loadSearch();
    }
    //开始加载搜索数据
    private void loadSearch(){
        searchContent=search_input.getText().toString();
        Log.d("搜索","开始搜索");
        if(searchContent.isEmpty()||searchContent.trim().equals("")){
            alert("搜索","请勿输入空值","确认");
            //Toast.makeText(MusiclistActivity.this,"请不要输入空值",Toast.LENGTH_LONG);
        }else{
            listDown();
            isList=false;
            list_change.setBackgroundResource(R.drawable.list1);
            if(!isSearch){
                isSearch=true;
                new PostTask(new PostTask.CallBack() {
                    @Override
                    public void getData(String result) {
                        Log.d("搜索","加载成功"+searchOffset);
                        ArrayList<Track> temp = JSONParser.TracksParer(result);
                        Log.d("歌单size",temp.size()+"");
                        if(temp.size()>1){
                            searchs.addAll(temp);
                            listView.setAdapter(searchAdapter);
                            searchAdapter.notifyDataSetChanged();
                            searchOffset+=temp.size();
                        }else if (searchOffset==0){
                            alert("","暂未搜索到相关歌曲","确认");
                            //Toast.makeText(MusiclistActivity.this,"暂未搜索到相关歌曲",Toast.LENGTH_LONG);
                        }
                        if(temp.size()<=0){
                            if(searchs.size()>0){
                                listDown("已经到底啦");
                            }else {
                                listDown("暂未搜索到相关信息");
                            }
                        }
                        isSearch=false;
                    }
                }).execute("http://music.163.com/api/search/pc",searchContent,searchOffset+"");
            }
        }
    }
    //切换回song列表
    private void changeToSong(){
        Log.d("歌单","添加歌曲");
        listView.setAdapter(adapter);
        isList=true;
        adapter.notifyDataSetChanged();
        if(data.size()<1)listDown("暂未搜索到相关曲目");
    }
    //更换歌单
    private void changeSongList(){
        Log.d("歌单","改变歌单");
        control.stopCorver();
        control.stopProgress();
        control.musicStop();
        data.clear();
        current=0;
        //search_input.setText("");
        needLoad=true;
        loadData(true);
    }
    //更换菜单按钮
    public void list_change(View v){
        Log.d("isList",isList+"");
        if(isList){
            if(searchs.size()<1){
                Log.d("isList",isList+"1");
                listDown("请输入搜索信息");
            }else{
                Log.d("isList",isList+"2");
                listDown();
            }
            list_change.setBackgroundResource(R.drawable.list1);
            listView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
            isList=false;
        }else{
            listDown();
            list_change.setBackgroundResource(R.drawable.list2);
            changeToSong();
        }
    }
    //弹窗alert
    private void alert(String title,String content,String button){
        builder  = new AlertDialog.Builder(MusiclistActivity.this);
        builder.setTitle(title) ;
        builder.setMessage(content) ;
        builder.setPositiveButton(button ,  null );
        builder.show();
        builder=null;
    }
}
