package com.example.administrator.myapplication.tookit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.administrator.myapplication.bean.Music;
import com.example.administrator.myapplication.bean.Track;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/6/10.
 */

public class SearchAdapter extends ArrayAdapter {
    private int resourceID;//列表项布局的ID
    public SearchAdapter(Context context, int resource, List<Track> objects) {
        super(context, resource, objects);
        resourceID=resource;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        SearchAdapter.ViewHolder holder=new SearchAdapter.ViewHolder();
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            holder.info= (TextView) view.findViewById(android.support.compat.R.id.info);
            holder.creator = (TextView) view.findViewById(android.support.compat.R.id.text);
            holder.count = (TextView) view.findViewById(android.support.compat.R.id.time);
            view.setTag(holder);
        }else{
            view = convertView;
            holder=(SearchAdapter.ViewHolder) view.getTag();
        }
        //final Map<String,Bitmap> map=new HashMap<String,Bitmap>();
        final Track track = (Track) getItem(position);
        holder.info.setText(track.getName());
        holder.creator.setText(track.getCreator());
        holder.count.setText(track.getCount()+"首");
        return view;
    }

    class ViewHolder{
        //        根据列表项中的控件来定义属性
        TextView info;
        TextView creator;
        TextView count;
    }
}
