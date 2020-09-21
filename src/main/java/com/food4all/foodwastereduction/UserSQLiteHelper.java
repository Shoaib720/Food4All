package com.food4all.foodwastereduction;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserSQLiteHelper extends SQLiteOpenHelper {

    public UserSQLiteHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists user (uid text, name text, contact text, email text, about text, dob text, district text, userType text, unique(email))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists user");
        onCreate(db);
    }
}
