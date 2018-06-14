package com.example.administrator.myapplication.tookit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.myapplication.bean.Music;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/4.
 */

public class MusicAdapter extends ArrayAdapter {
    private int resourceID;//列表项布局的ID
    public MusicAdapter(Context context, int resource, List<Music> objects) {
        super(context, resource, objects);
        resourceID=resource;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        ViewHolder holder=new ViewHolder();
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            holder.info= (TextView) view.findViewById(android.support.compat.R.id.info);
            holder.singer = (TextView) view.findViewById(android.support.compat.R.id.text);
            holder.time = (TextView) view.findViewById(android.support.compat.R.id.time);
            view.setTag(holder);
        }else{
            view = convertView;
            holder=(ViewHolder) view.getTag();
        }
        //final Map<String,Bitmap> map=new HashMap<String,Bitmap>();
        final Music music = (Music) getItem(position);
        holder.info.setText(music.getName());
        holder.singer.setText(music.getArtists());
        holder.time.setText(timeTotime(music.getTime()));
        return view;
    }

    class ViewHolder{
        //        根据列表项中的控件来定义属性
        TextView info;
        TextView singer;
        TextView time;
    }
    public String timeTotime(int time){
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        return sd;
    }
}
