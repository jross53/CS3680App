package com.jrsqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jrsqlite.AppConstants;

/**
 * Created by Jordan.Ross on 2/25/2017.
 */

public class CourseDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "CourseList.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CourseContract.CourseEntry.TABLE_NAME + " (" +
                    CourseContract.CourseEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_INSTRUCTOR + " TEXT NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_NUMBER + " TEXT NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_CAPACITY + " INTEGER NOT NULL," +
                    CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + ") " +
                    "REFERENCES " + DepartmentContract.DepartmentEntry.TABLE_NAME +
                    "(" + DepartmentContract.DepartmentEntry._ID + "))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CourseContract.CourseEntry.TABLE_NAME;

    public CourseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i(AppConstants.APP_TAG, SQL_CREATE_ENTRIES);
        try {
            db.execSQL(SQL_CREATE_ENTRIES);
        } catch(Exception ex) {
            Log.e(AppConstants.APP_TAG, "CourseDbHelper onCreate error: " + ex.getMessage());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.i(AppConstants.APP_TAG, SQL_DELETE_ENTRIES);
        try {
            db.execSQL(SQL_DELETE_ENTRIES);
        } catch(Exception ex) {
            Log.e(AppConstants.APP_TAG, "CourseDbHelper onUpgrade error: " + ex.getMessage());
        }
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
