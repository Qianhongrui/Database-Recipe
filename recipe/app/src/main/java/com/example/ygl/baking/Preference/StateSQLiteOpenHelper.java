package com.example.ygl.baking.Preference;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


// 继承自SQLiteOpenHelper数据库类的子类
public class StateSQLiteOpenHelper extends SQLiteOpenHelper {

    private static String name = "state_DB.db";
    private static Integer version = 1;

    public StateSQLiteOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 打开数据库 & 建立了一个叫records的表，里面只有一列name来存储历史记录：
        db.execSQL("create table states(id integer primary key autoincrement,state_name varchar(10),state int(1))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
