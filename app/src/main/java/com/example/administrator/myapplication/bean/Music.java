package com.example.administrator.myapplication.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/4.
 */

public class Music implements Serializable {
    private int id;
    private String name;
    private String artists;
    private String album;
    private String cover;
    private Bitmap corverImg;
    private Bitmap backImg;
    private int time;

    public Bitmap getBackImg() {
        return backImg;
    }

    public void setBackImg(Bitmap backImg) {
        this.backImg = backImg;
    }

    public Bitmap getCorverImg() {
        return corverImg;
    }

    public void setCorverImg(Bitmap corverImg) {
        this.corverImg = corverImg;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Music(int id, String name, String artists, String album, String cover, int time) {

        this.id = id;
        this.name = name;
        this.artists = artists;
        this.album = album;
        this.cover = cover;
        this.time = time;
    }
}
