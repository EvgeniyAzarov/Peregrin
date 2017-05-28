package com.peregrin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "peregrin", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table chats_list ("
                + "id integer primary key autoincrement,"
                + "interlocutor_login text,"
                + "interlocutor_nickname text);");

        db.execSQL("create table messages ("
                + "sender_login text, "
                + "recipient_login text, "
                + "content text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
