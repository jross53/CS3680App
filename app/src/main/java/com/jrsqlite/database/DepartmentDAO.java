package com.jrsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jrsqlite.AppConstants;
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

    private Cursor getCursorForCourseSelect(final String selection, final String[] selectionArgs) {
        Cursor cursor = db.query(
                CourseContract.CourseEntry.TABLE_NAME,                     // The table to query
                ALL_COLUMNS,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        Log.i(AppConstants.APP_TAG, "Found  " + cursor.getCount() + " courses");

        return cursor;
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

        Log.i(AppConstants.APP_TAG, "Found  " + cursor.getCount() + " departments");

        return cursor;
    }

    private List<CollegeDepartment> findAllBy(final String selection,
                                              final String[] selectionArgs) {
        List<CollegeDepartment> collegeDepartmentList = new ArrayList<>();
        Cursor cursor = getCursorForSelect(selection, selectionArgs);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            CollegeDepartment collegeDepartment = new CollegeDepartment(name, id);
            collegeDepartmentList.add(collegeDepartment);
            Log.i(AppConstants.APP_TAG, "name: " + name + " id: " + id);
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

    public int getComputerScienceDepartmentId() {
        List<CollegeDepartment> allByName = findAllByName(DepartmentConstants.COMPUTER_SCIENCE);
        if(allByName == null || allByName.size() == 0) {
            return -1;
        }
        return allByName.get(0).getId();
    }

    public int getNetworkingDepartmentId() {
        List<CollegeDepartment> allByName = findAllByName(DepartmentConstants.NETWORKING);
        if(allByName == null || allByName.size() == 0) {
            return -1;
        }
        return allByName.get(0).getId();
    }

    public int getInformationTechnologyDepartmentId() {
        List<CollegeDepartment> allByName = findAllByName(DepartmentConstants.INFORMATION_TECHNOLOGY);
        if(allByName == null || allByName.size() == 0) {
            return -1;
        }
        return allByName.get(0).getId();
    }

    public int getElectricalEngineeringDepartmentId() {
        List<CollegeDepartment> allByName = findAllByName(DepartmentConstants.ELECTRICAL_ENGINEERING);
        if(allByName == null || allByName.size() == 0) {
            return -1;
        }
        return allByName.get(0).getId();
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
        Log.i(AppConstants.APP_TAG, numberOfRowsDeleted + " rows deleted");

//        deleteAllCourses();
        return numberOfRowsDeleted > -1;
    }

//    public boolean deleteAllCourses() {
//        int numberOfRowsDeleted = db.delete(CourseContract.CourseEntry.TABLE_NAME, null, null);
//        Log.i(AppConstants.APP_TAG, "DepartmentDAO deleteAllCourses " + numberOfRowsDeleted + " rows deleted");
//        return numberOfRowsDeleted > -1;
//    }

    public boolean deleteDepartment(final CollegeDepartment collegeDepartment) {
        try {
            int numberOfRowsDeleted = db.delete(DepartmentContract.DepartmentEntry.TABLE_NAME, "name = ? ", new String[]{collegeDepartment.getName()});
            Log.i(AppConstants.APP_TAG, "DepartmentDAO deleteDepartment " + numberOfRowsDeleted + " rows deleted");

            //            if(deletedDepartment) {
//                if(deleteAllCoursesForDepartment(collegeDepartment.getId())) {
//                    Log.e(AppConstants.APP_TAG, "Unable to delete all courses for department id: "
//                            + collegeDepartment.getId());
//                }
//            }

            return numberOfRowsDeleted == 1;
        } catch (Exception ex) {
            Log.e(AppConstants.APP_TAG, "deleteDepartment error: " + ex.getMessage());
        }

        return true;
    }

//    public boolean deleteAllCoursesForDepartment(int departmentId) {
//        Log.i(AppConstants.APP_TAG, "Deleting all courses for department id: " + departmentId);
//        List<CollegeCourse> collegeCourses = findAllByDepartment(departmentId);
//        Log.i(AppConstants.APP_TAG, "Deleting " + collegeCourses.size() + " courses for department id: " + departmentId);
//        boolean deletedAllCourses = true;
//
//        for (CollegeCourse collegeCourse : collegeCourses) {
//            if (!deleteCourse(collegeCourse.getId())) {
//                deletedAllCourses = false;
//            }
//        }
//
//        return deletedAllCourses;
//    }

//    public boolean deleteCourse(final int id) {
//        int numberOfRowsDeleted = db.delete(CourseContract.CourseEntry.TABLE_NAME, "id = ? ", new String[]{String.valueOf(id)});
//        Log.i(AppConstants.APP_TAG, "DepartmentDAO deleteCourse " + numberOfRowsDeleted + " rows deleted");
//        return numberOfRowsDeleted == 1;
//    }
//
//    public List<CollegeCourse> findAllByDepartment(final int departmentId) {
//        String selection = CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " = ?";
//        String[] selectionArgs = {String.valueOf(departmentId)};
//
//        return findAllCoursesBy(selection, selectionArgs);
//    }

//    private List<CollegeCourse> findAllCoursesBy(final String selection,
//                                                 final String[] selectionArgs) {
//        List<CollegeCourse> collegeCourseList = new ArrayList<>();
//        Log.i(AppConstants.APP_TAG, "In findAllCoursesBy. selection: " + selection
//                + " selectionArgs: " + Arrays.toString(selectionArgs));
//        try {
//            Cursor cursor = getCursorForCourseSelect(selection, selectionArgs);
//
//            while (cursor.moveToNext()) {
//                int id = cursor.getInt(0);
//                String name = cursor.getString(1);
//                String instructor = cursor.getString(2);
//                String number = cursor.getString(3);
//                int capacity = cursor.getInt(4);
//                int departmentId = cursor.getInt(5);
//                CollegeCourse collegeCourse =
//                        new CollegeCourse(id, name, capacity, instructor, number, departmentId);
//                collegeCourseList.add(collegeCourse);
//            }
//            cursor.close();
//        } catch (Exception ex) {
//            Log.e(AppConstants.APP_TAG, "Error in findAllCoursesBy. selection: " + selection
//                    + " selectionArgs: " + Arrays.toString(selectionArgs) + " " + ex.getMessage());
//        }
//
//        return collegeCourseList;
//    }

    private List<CollegeDepartment> getTestCollegeDepartmentList() {
        List<CollegeDepartment> collegeDepartments = new ArrayList<>();
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.COMPUTER_SCIENCE));
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.GRAPHICAL_DESIGN));
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.NETWORKING));
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.INFORMATION_TECHNOLOGY));
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.ELECTRICAL_ENGINEERING));
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.CYBER_SECURITY));
        collegeDepartments.add(new CollegeDepartment(DepartmentConstants.COMPUTER_ENGINEERING));

        return collegeDepartments;
    }
}
