package com.jrsqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jordan.Ross on 2/25/2017.
 */

public class CourseDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "CourseList.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CourseContract.CourseEntry.TABLE_NAME + " (" +
                    CourseContract.CourseEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_INSTRUCTOR + " TEXT NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_NUMBER + " TEXT NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_CAPACITY + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CourseContract.CourseEntry.TABLE_NAME;

    public CourseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
