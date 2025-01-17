package com.example.memo_test;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class MemoHelper extends SQLiteOpenHelper {

    static final private String DBName = "Memo_DB";
    static final private int VERSION = 4;

    public MemoHelper(Context context){
        super(context, DBName, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE MEMO_TABLE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uuid TEXT, " +
                "body TEXT)");

        db.execSQL("CREATE TABLE DATA_TABLE (" +
                "ref_uuid TEXT PRIMARY KEY, " +
                "data TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS MEMO_TABLE");
        db.execSQL("DROP TABLE IF EXISTS DATA_TABLE");
        onCreate(db);
    }
}
