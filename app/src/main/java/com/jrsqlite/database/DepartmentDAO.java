package com.jrsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jrsqlite.CollegeCourse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan.Ross on 2/28/2017.
 */

public class CourseDAO {

    private static final String COURSE_DAO = "CourseDAO";
    private SQLiteDatabase db;
    private static String[] ALL_COLUMNS = {
            CourseContract.CourseEntry._ID,
            CourseContract.CourseEntry.COLUMN_NAME,
            CourseContract.CourseEntry.COLUMN_INSTRUCTOR,
            CourseContract.CourseEntry.COLUMN_NUMBER,
            CourseContract.CourseEntry.COLUMN_CAPACITY
    };

    public CourseDAO(final Context context) {
        final CourseDbHelper mDbHelper = new CourseDbHelper(context);
        db = mDbHelper.getWritableDatabase();
    }

    public boolean saveCourse(final CollegeCourse collegeCourse) {
        ContentValues values = new ContentValues();
        values.put(CourseContract.CourseEntry.COLUMN_NAME, collegeCourse.getName());
        values.put(CourseContract.CourseEntry.COLUMN_INSTRUCTOR, collegeCourse.getInstructor());
        values.put(CourseContract.CourseEntry.COLUMN_NUMBER, collegeCourse.getNumber());
        values.put(CourseContract.CourseEntry.COLUMN_CAPACITY, collegeCourse.getCapacity());

        long newRowId = db.insert(CourseContract.CourseEntry.TABLE_NAME, null, values);
        return newRowId > 0;
    }

    private Cursor getCursorForSelect(final String selection, final String[] selectionArgs) {
        Cursor cursor = db.query(
                CourseContract.CourseEntry.TABLE_NAME,                     // The table to query
                ALL_COLUMNS,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        Log.i(COURSE_DAO, "Found  " + cursor.getCount() + " courses");

        return cursor;
    }

    private List<CollegeCourse> findAllBy(final String selection,
                                          final String[] selectionArgs) {
        List<CollegeCourse> collegeCourseList = new ArrayList<>();
        Cursor cursor = getCursorForSelect(selection, selectionArgs);

        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String instructor = cursor.getString(2);
            String number = cursor.getString(3);
            int capacity = cursor.getInt(4);
            CollegeCourse collegeCourse = new CollegeCourse(name, capacity, instructor, number);
            collegeCourseList.add(collegeCourse);
//            Log.i(COURSE_DAO, "name: " + name + " instructor: " + instructor + " number: " + number + " capacity: " + capacity);
        }
        cursor.close();

        return collegeCourseList;
    }

    public List<CollegeCourse> findAll() {
        return findAllBy(null, null);
    }

    public List<CollegeCourse> findAllByName(final String name) {
        String selection = CourseContract.CourseEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + name + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByNumber(final String number) {
        String selection = CourseContract.CourseEntry.COLUMN_NUMBER + " LIKE ?";
        String[] selectionArgs = {"%" + number + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByInstructor(final String instructor) {
        String selection = CourseContract.CourseEntry.COLUMN_INSTRUCTOR + " LIKE ?";
        String[] selectionArgs = {"%" + instructor + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByCapacity(final int capacity) {
        String selection = CourseContract.CourseEntry.COLUMN_CAPACITY + " LIKE ?";
        String[] selectionArgs = {String.valueOf(capacity)};

        return findAllBy(selection, selectionArgs);
    }

    public boolean insertTestCourses() {
        final List<CollegeCourse> collegeCourses = getTestCollegeCourseList();
        for (CollegeCourse collegeCourse : collegeCourses) {
            if (!saveCourse(collegeCourse)) {
                deleteAllCourses();
                return false;
            }
        }

        return true;
    }

    public boolean deleteAllCourses() {
        int numberOfRowsDeleted = db.delete(CourseContract.CourseEntry.TABLE_NAME, null, null);
        Log.i(COURSE_DAO, numberOfRowsDeleted + " rows deleted");
        return numberOfRowsDeleted > -1;
    }

    public boolean deleteCourse(final CollegeCourse collegeCourse) {
        int numberOfRowsDeleted = db.delete(CourseContract.CourseEntry.TABLE_NAME, "name = ? ", new String[] { collegeCourse.getName() });
        Log.i(COURSE_DAO, numberOfRowsDeleted + " rows deleted");
        return numberOfRowsDeleted == 1;
    }

    private List<CollegeCourse> getTestCollegeCourseList() {
        List<CollegeCourse> collegeCourses = new ArrayList<>();
        collegeCourses.add(new CollegeCourse("Mobile App Development", 35, "Durney", "CS 3680"));
        collegeCourses.add(new CollegeCourse("Discrete Structures", 25, "Jack", "CS 3681"));
        collegeCourses.add(new CollegeCourse("Data Structures", 10, "Smoot", "CS 3682"));
        collegeCourses.add(new CollegeCourse("Intro to programming", 45, "Smit", "CS 3683"));
        collegeCourses.add(new CollegeCourse("Intro to computers", 30, "Stevens", "CS 3684"));
        collegeCourses.add(new CollegeCourse("Programming in Assembly", 35, "Clark", "CS 3685"));
        collegeCourses.add(new CollegeCourse("Web Development", 35, "Jeter", "CS 3686"));
        collegeCourses.add(new CollegeCourse("Database Theory", 35, "Bryant", "CS 3687"));
        collegeCourses.add(new CollegeCourse("MongoDB", 35, "Curry", "CS 3688"));
        collegeCourses.add(new CollegeCourse("Linux Administration", 40, "Johnson", "CS 3689"));
        collegeCourses.add(new CollegeCourse("Advanced Python", 35, "Smith", "CS 3690"));
        collegeCourses.add(new CollegeCourse("Advanced Java", 35, "Benji", "CS 3691"));
        collegeCourses.add(new CollegeCourse("Advanced C++", 25, "Durney", "CS 3692"));
        collegeCourses.add(new CollegeCourse("Advanced C#", 45, "Matthews", "CS 3693"));
        collegeCourses.add(new CollegeCourse("Windows Administration", 35, "Jeffries", "CS 3694"));
        collegeCourses.add(new CollegeCourse("Docker", 35, "Sky", "CS 3695"));
        collegeCourses.add(new CollegeCourse("Discrete Math", 30, "Max", "CS 3696"));
        collegeCourses.add(new CollegeCourse("Big Data", 30, "Griffin", "CS 3697"));
        collegeCourses.add(new CollegeCourse("Artificial Intelligence", 35, "Jordan", "CS 3698"));
        collegeCourses.add(new CollegeCourse("Compilers", 40, "Jabbar", "CS 3699"));

        return collegeCourses;
    }
}
