package com.jrsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jrsqlite.CollegeDepartment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan.Ross on 2/28/2017.
 */

public class DepartmentDAO {

    private static final String DEPARTMENT_TAG = "DepartmentDAO";
    private SQLiteDatabase db;
    private static String[] ALL_COLUMNS = {
            DepartmentContract.DepartmentEntry._ID,
            DepartmentContract.DepartmentEntry.COLUMN_NAME,
    };

    public DepartmentDAO(final Context context) {
        final DepartmentDbHelper mDbHelper = new DepartmentDbHelper(context);
        db = mDbHelper.getWritableDatabase();
    }

    public boolean saveDepartment(final CollegeDepartment collegeDepartment) {
        ContentValues values = new ContentValues();
        values.put(DepartmentContract.DepartmentEntry.COLUMN_NAME, collegeDepartment.getName());

        long newRowId = db.insert(DepartmentContract.DepartmentEntry.TABLE_NAME, null, values);
        return newRowId > 0;
    }

    private Cursor getCursorForSelect(final String selection, final String[] selectionArgs) {
        Cursor cursor = db.query(
                DepartmentContract.DepartmentEntry.TABLE_NAME,                     // The table to query
                ALL_COLUMNS,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        Log.i(DEPARTMENT_TAG, "Found  " + cursor.getCount() + " departments");

        return cursor;
    }

    private List<CollegeDepartment> findAllBy(final String selection,
                                          final String[] selectionArgs) {
        List<CollegeDepartment> collegeDepartmentList = new ArrayList<>();
        Cursor cursor = getCursorForSelect(selection, selectionArgs);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            CollegeDepartment collegeDepartment = new CollegeDepartment(name);
            collegeDepartmentList.add(collegeDepartment);
            Log.i(DEPARTMENT_TAG, "name: " + name + " id: " + id);
        }
        cursor.close();

        return collegeDepartmentList;
    }

    public List<CollegeDepartment> findAll() {
        return findAllBy(null, null);
    }

    public List<CollegeDepartment> findAllByName(final String name) {
        String selection = DepartmentContract.DepartmentEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + name + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public boolean insertTestDepartments() {
        final List<CollegeDepartment> collegeDepartments = getTestCollegeDepartmentList();
        for (CollegeDepartment collegeDepartment : collegeDepartments) {
            if (!saveDepartment(collegeDepartment)) {
                deleteAllDepartments();
                return false;
            }
        }

        return true;
    }

    public boolean deleteAllDepartments() {
        int numberOfRowsDeleted = db.delete(DepartmentContract.DepartmentEntry.TABLE_NAME, null, null);
        Log.i(DEPARTMENT_TAG, numberOfRowsDeleted + " rows deleted");
        return numberOfRowsDeleted > -1;
    }

    public boolean deleteDepartment(final CollegeDepartment collegeDepartment) {
        int numberOfRowsDeleted = db.delete(DepartmentContract.DepartmentEntry.TABLE_NAME, "name = ? ", new String[] { collegeDepartment.getName() });
        Log.i(DEPARTMENT_TAG, numberOfRowsDeleted + " rows deleted");
        return numberOfRowsDeleted == 1;
    }

    private List<CollegeDepartment> getTestCollegeDepartmentList() {
        List<CollegeDepartment> collegeDepartments = new ArrayList<>();
        collegeDepartments.add(new CollegeDepartment("Computer Science"));
        collegeDepartments.add(new CollegeDepartment("Graphical Design"));
        collegeDepartments.add(new CollegeDepartment("Networking"));
        collegeDepartments.add(new CollegeDepartment("Information Technology"));
        collegeDepartments.add(new CollegeDepartment("Electrical Engineering"));
        collegeDepartments.add(new CollegeDepartment("Cyber Security"));
        collegeDepartments.add(new CollegeDepartment("Computer Engineering"));

        return collegeDepartments;
    }
}
