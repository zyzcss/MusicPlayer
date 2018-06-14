package com.example.administrator.myapplication.bean;

/**
 * Created by Administrator on 2018/6/10.
 */

public class Track {
    private int id;
    private String name;
    private int count;
    private String creator;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Track(int id, String name, int count, String creator) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.creator = creator;
    }

}
