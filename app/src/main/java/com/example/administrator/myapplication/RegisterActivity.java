package com.example.administrator.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.myapplication.tookit.SqlLite;

public class RegisterActivity extends AppCompatActivity {
    EditText user,pass,repass;
    SqlLite helper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user = (EditText) findViewById(R.id.regUser);
        pass = (EditText) findViewById(R.id.regPass);
        repass = (EditText) findViewById(R.id.regPassRe);
    }
    public void register(View v){
        String accountString = user.getText().toString();
        String passwordString = pass.getText().toString();
        String confirmString = repass.getText().toString();
        if(TextUtils.isEmpty(accountString) || TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(confirmString)){
            Toast.makeText(this,"账号密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(!passwordString.equals(confirmString)){
                Toast.makeText(this,"两次密码不同",Toast.LENGTH_SHORT).show();
                return;
            }else{
                helper=new SqlLite(this,"mydb.db",null,1);
                db=helper.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from user where username = ?",new String[]{accountString});

                if(cursor.moveToFirst()){
                    Toast.makeText(this,"用户名已存在",Toast.LENGTH_SHORT).show();
                }else{
                    helper=new SqlLite(this,"mydb.db",null,1);
                    db=helper.getWritableDatabase();
                    //判断账号是否存在
                    //把账号密码写入数据库
                    db.execSQL("insert into user (username,password) values(?,?)",new String[]{accountString.trim(),passwordString});
                    db.close();//关闭数据库
                    finish();//结束当前活动 回到前面活动  注册完毕
                }
            }
        }
    }
}
