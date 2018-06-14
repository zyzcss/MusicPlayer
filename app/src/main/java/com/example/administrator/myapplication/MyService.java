package com.example.administrator.myapplication;

/**
 * Created by Administrator on 2018/6/7.
 */

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service{
    MediaPlayer player=null;
    Timer timer,timer2;
    TimerTask task,task2;
    float deg=0;
    Boolean flag=true;
    public MyService() {
        Log.d("音乐","实例化");
    }
    int current;
    String url="";
    public synchronized void  musicPlay(int position){
        Log.d("音乐","开始播放");
        if(flag){
            if(player==null){
                Log.d("音乐","初始化下载");
                //player=MediaPlayer.create(MyService.this,R.raw.ad);
                player=new MediaPlayer();
                try {
                    player.setDataSource("http://music.163.com/song/media/outer/url?id="+url+".mp3");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepareAsync();
                player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        Log.d("音乐","缓存");
                    }
                });
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.d("音乐","播放");
                        deg=0;
                        stopCorver();
                        stopProgress();
                        player.start();
                        updateProgress();
                        updateCorver();
                    }
                });
            }else if(!player.isPlaying()){
                Log.d("音乐","已经有了"+position);
                player.seekTo(position);
                stopProgress();
                stopCorver();
                player.start();
                updateProgress();
                updateCorver();
            }
        }
    }
    public void musicPause(){
        if(player!=null&&player.isPlaying()){
            player.pause();
            pauseProgress();
            pauseCorver();
        }
    }
    public void musicStop(){
        if(player!=null){
            player.stop();
            player.release();
            player=null;
            stopProgress();
            stopCorver();

        }
    }
    public void updateProgress() {
            timer=new Timer();
            task=new TimerTask() {
                @Override
                public synchronized void run() {
                    if(player!=null){
                        try {
                            if(player.isPlaying())current=player.getCurrentPosition()>0?player.getCurrentPosition():0;
                        }
                        catch (IllegalStateException e) {
                            Log.d("====","========="+"illerror");
                        }
                        Intent intent=new Intent("com.seekbar");
                        intent.putExtra("current",current);
                        intent.putExtra("type",2);
                        sendBroadcast(intent);
                    }
                }
            };
            timer.schedule(task,0,1000);
    }
    public void stopProgress(){
        Log.d("进度条","停止");
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        if(task!=null){
            task.cancel();
            task=null;
        }
        Intent intent=new Intent("com.seekbar");
        intent.putExtra("current",current);
        intent.putExtra("type",2);
        sendBroadcast(intent);
    }
    public void pauseProgress(){
        if(task!=null){
            task.cancel();
        }
        if(timer!=null){
            timer.cancel();
        }
    }

    public void updateCorver() {
        timer2=new Timer();
        task2=new TimerTask() {
            @Override
            public void run() {
                if(deg<360){
                    deg+=0.5;
                }else{
                    deg=0;
                }
                Intent intent=new Intent("com.seekbar");
                intent.putExtra("deg",deg);
                intent.putExtra("type",1);
                sendBroadcast(intent);
            }
        };
        timer2.schedule(task2,0,85);
    }
    public void pauseCorver(){
        if(task2!=null){
            task2.cancel();
        }
        if(timer2!=null){
            timer2.cancel();
        }
    }
    public void stopCorver(){
        Log.d("封面","停止");
        if(task2!=null){
            task2.cancel();
        }
        if(timer2!=null){
            timer2.purge();
            timer2.cancel();
        }
        Intent intent=new Intent("com.seekbar");
        intent.putExtra("deg",deg);
        intent.putExtra("type",1);
        sendBroadcast(intent);
    }
    public void musicSeekTo(int position){
        if(player!=null){
            player.seekTo(position);
        }
    }


    public class MusicControl extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("音乐","绑定成功");
        return new MusicControl();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //player.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player!=null){
            player.stop();;
            player.release();
            player=null;
        }
    }
}
