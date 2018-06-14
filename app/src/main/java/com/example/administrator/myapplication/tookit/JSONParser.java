package com.example.administrator.myapplication.tookit;

import android.util.Log;

import com.example.administrator.myapplication.bean.Music;
import com.example.administrator.myapplication.bean.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/4.
 */
public class JSONParser {
    public static ArrayList<Music> MyJsonParer(String data){
        ArrayList<Music> result = new ArrayList<Music>();
        try {
            JSONObject object1 = new JSONObject(data);
            if(object1.has("result")){
                JSONObject object2 = object1.getJSONObject("result");
                JSONArray jsonArray1 = object2.getJSONArray("tracks");
                for(int i = 0 ; i <jsonArray1.length();i++){
                    JSONObject object3 = jsonArray1.getJSONObject(i);
                    //JSONObject object4 = object3.getJSONObject("data");
                    int id=object3.getInt("id");
                    String name = object3.getString("name");
                    String artists=getArtists(object3.getJSONArray("artists"));
                    String cover=object3.getJSONObject("album").getString("picUrl");
                    String album=object3.getJSONObject("album").getString("name");
                    int time = object3.getJSONObject("bMusic").getInt("playTime");
                    Music music = new Music(id,name,artists,album,cover,time);
                    music.setCorverImg(null);
                    result.add(music);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static ArrayList<Track> TracksParer(String data){
        ArrayList<Track> result = new ArrayList<Track>();
        try {
            JSONObject object1 = new JSONObject(data);
            Log.d("on",data);
            JSONObject object2 = object1.getJSONObject("result");
            if(object2.has("playlists")){
                JSONArray jsonArray1 = object2.getJSONArray("playlists");
                for(int i = 0 ; i <jsonArray1.length();i++){
                    JSONObject object3 = jsonArray1.getJSONObject(i);
                    int id=object3.getInt("id");
                    String name = object3.getString("name");
                    int count = object3.getInt("trackCount");
                    String creator = object3.getJSONObject("creator").getString("nickname");
                    Track track=new Track(id,name,count,creator);
                    result.add(track);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static String getArtists(JSONArray strings){
        String data="";
        try {
            for (int i=0;i<strings.length()-1;i++){
                    data+=strings.getJSONObject(i).getString("name")+"/";
            }
            data+=strings.getJSONObject(strings.length()-1).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}