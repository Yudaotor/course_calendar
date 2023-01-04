package com.example.a2022finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Time;
//数据库操作类
public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(@Nullable Context context) {
        super(context, "class.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //初始化数据库，存入一些基本课程信息
        db.execSQL("CREATE TABLE class(weekNo VATCHAR(20), name VARCHAR(20), time VATCHAR(20), room VARCHAR(20))");
        ContentValues values = new ContentValues();
        values.put("weekNo", "1");
        values.put("name", "Web应用程序开发");
        values.put("time", "09:40");
        values.put("room", "29-106");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "1");
        values.put("name", "智能移动设备软件开发");
        values.put("time", "14:00");
        values.put("room", "29-106");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "2");
        values.put("name", "编译原理实验");
        values.put("time", "10:35");
        values.put("room", "29-106");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "2");
        values.put("name", "软件项目管理");
        values.put("time", "14:00");
        values.put("room", "23-213");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "2");
        values.put("name", "软件项目管理实验");
        values.put("time", "15:40");
        values.put("room", "20-109");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "3");
        values.put("name", "马克思主义基本原理");
        values.put("time", "08:00");
        values.put("room", "16-103");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "4");
        values.put("name", "编译原理");
        values.put("time", "10:35");
        values.put("room", "23-319");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "5");
        values.put("name", "Python与数据分析");
        values.put("time", "08:45");
        values.put("room", "25-315");
        db.insert("class", null, values);
        values = new ContentValues();
        values.put("weekNo", "5");
        values.put("name", "Python与数据分析实验");
        values.put("time", "10:35");
        values.put("room", "20-206");
        db.insert("class", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
