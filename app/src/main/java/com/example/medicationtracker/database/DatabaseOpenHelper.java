package com.example.medicationtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jia Hao on 5/31/2017.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "drugs.db";
    public static final String TABLE = "drugs";
    public static final int VERSION = 3;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE drugs (name STRING PRIMARY KEY, dosage STRING, remarks STRING, hour INTEGER, minute INTEGER, thumbnail BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS drugs");
        onCreate(db);
    }
}