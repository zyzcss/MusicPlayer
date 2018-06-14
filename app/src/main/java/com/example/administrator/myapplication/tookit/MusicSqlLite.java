package com.example.administrator.myapplication.tookit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/6/11.
 */

public class MusicSqlLite extends SQLiteOpenHelper {
    private Context context;
    public static final String SQL="create table music (" +  //创建表的sql语句
            "ids integer primary key,"+
            "id integer," +
            "name text," +
            "album text," +
            "cover text," +
            "time integer," +
            "artists text)";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    public MusicSqlLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
