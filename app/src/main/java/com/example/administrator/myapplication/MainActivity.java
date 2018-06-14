package com.example.administrator.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.myapplication.tookit.SqlLite;

public class MainActivity extends AppCompatActivity {
    EditText username,password;
    CheckBox AutoLogin;
    SqlLite helper;
    SQLiteDatabase db;
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                return;
            }
        }
        spf= PreferenceManager.getDefaultSharedPreferences(this);
        AutoLogin = (CheckBox) findViewById(R.id.isRemember);
        username= (EditText) findViewById(R.id.loginUser);
        password = (EditText) findViewById(R.id.loginPass);
        boolean auto=spf.getBoolean("auto",false);
        if(auto){
            String stringaccount=spf.getString("accountString","");
            String stringpassword=spf.getString("passwordString","");
            username.setText(stringaccount);
            password.setText(stringpassword);
            AutoLogin.setChecked(auto);
        }
    }
    public void goregister(View v){
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
    public void login(View v){
        String accountString = ((EditText) findViewById(R.id.loginUser)).getText().toString();
        String passwordString = ((EditText) findViewById(R.id.loginPass)).getText().toString();
        if(TextUtils.isEmpty(accountString) || TextUtils.isEmpty(passwordString)){
            Toast.makeText(MainActivity.this,"账号密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        helper=new SqlLite(MainActivity.this,"mydb.db",null,1);
        db=helper.getWritableDatabase();
        Log.d(passwordString.trim(),accountString.trim());
        Cursor cursor = db.rawQuery("select * from user where username = ? and password = ?",new String[]{accountString.trim(),passwordString.trim()});
        if(cursor.moveToFirst()){
            //如果条件成立就有数据
            Toast.makeText(MainActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
            //自动登录保存数据
            boolean isAutoLogin=AutoLogin.isChecked();
            editor=spf.edit();
            if(isAutoLogin){
                editor.putString("accountString",accountString);
                editor.putString("passwordString",passwordString);
                editor.putBoolean("auto",isAutoLogin);
            }else{
                editor.clear();
            }
            editor.apply();
            Intent intent=new Intent(this,MusiclistActivity.class);
            startActivity(intent);
            //finish();
        }else{
            Toast.makeText(MainActivity.this,"账号密码错误或者不存在",Toast.LENGTH_SHORT).show();
        }
    }
    public void goWeb(View v){
        Intent intent=new Intent(MainActivity.this,WebViewActivity.class);
        startActivity(intent);
    }
}

