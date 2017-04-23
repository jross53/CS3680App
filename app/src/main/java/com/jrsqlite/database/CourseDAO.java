package com.jrsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jrsqlite.AppConstants;
import com.jrsqlite.CollegeCourse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jordan.Ross on 2/28/2017.
 */

public class CourseDAO {

    private DepartmentDAO departmentDAO;

    private static final String COURSE_DAO = "CourseDAO";
    private SQLiteDatabase db;
    private static String[] ALL_COLUMNS = {
            CourseContract.CourseEntry._ID,
            CourseContract.CourseEntry.COLUMN_NAME,
            CourseContract.CourseEntry.COLUMN_INSTRUCTOR,
            CourseContract.CourseEntry.COLUMN_NUMBER,
            CourseContract.CourseEntry.COLUMN_CAPACITY,
            CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID
    };

    public CourseDAO(final Context context) {
        final CourseDbHelper mDbHelper = new CourseDbHelper(context);
        db = mDbHelper.getWritableDatabase();
        departmentDAO = new DepartmentDAO(context);
    }

    public int saveCourse(final CollegeCourse collegeCourse) {
        try {
            ContentValues values = new ContentValues();
            values.put(CourseContract.CourseEntry.COLUMN_NAME, collegeCourse.getName());
            values.put(CourseContract.CourseEntry.COLUMN_INSTRUCTOR, collegeCourse.getInstructor());
            values.put(CourseContract.CourseEntry.COLUMN_NUMBER, collegeCourse.getNumber());
            values.put(CourseContract.CourseEntry.COLUMN_CAPACITY, collegeCourse.getCapacity());
            values.put(CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID, collegeCourse.getDepartmentId());
            Log.i(AppConstants.APP_TAG, "saveCourse name: " + collegeCourse.getName() +
                    " department id: " + collegeCourse.getDepartmentId());

            long newRowId = db.insert(CourseContract.CourseEntry.TABLE_NAME, null, values);
            return (int) newRowId;
        } catch (Exception ex) {
            Log.e(AppConstants.APP_TAG, "Error inserting course: " + ex.getMessage());
        }
        return -1;
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
        Log.i(AppConstants.APP_TAG, "In findAllBy. selection: " + selection
                + " selectionArgs: " + Arrays.toString(selectionArgs));
        try {
            Cursor cursor = getCursorForSelect(selection, selectionArgs);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String instructor = cursor.getString(2);
                String number = cursor.getString(3);
                int capacity = cursor.getInt(4);
                int departmentId = cursor.getInt(5);
                CollegeCourse collegeCourse =
                        new CollegeCourse(id, name, capacity, instructor, number, departmentId);
                collegeCourseList.add(collegeCourse);
            }
            cursor.close();
        } catch (Exception ex) {
            Log.e(AppConstants.APP_TAG, "Error in findAllBy. selection: " + selection
                    + " selectionArgs: " + Arrays.toString(selectionArgs) + " " + ex.getMessage());
        }

        return collegeCourseList;
    }

    public List<CollegeCourse> findAllByName(final String name, final int departmentId) {
        String selection = CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " = ? AND " +
                CourseContract.CourseEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {String.valueOf(departmentId), "%" + name + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByNumber(final String number, final int departmentId) {
        String selection = CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " = ? AND " +
                CourseContract.CourseEntry.COLUMN_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(departmentId), "%" + number + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByInstructor(final String instructor, final int departmentId) {
        String selection = CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " = ? AND " +
                CourseContract.CourseEntry.COLUMN_INSTRUCTOR + " LIKE ?";
        String[] selectionArgs = {String.valueOf(departmentId), "%" + instructor + "%"};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByCapacity(final int capacity, final int departmentId) {
        String selection = CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " = ? AND " +
                CourseContract.CourseEntry.COLUMN_CAPACITY + " LIKE ?";
        String[] selectionArgs = {String.valueOf(departmentId), String.valueOf(capacity)};

        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAllByDepartment(final int departmentId) {
//        Cursor cursor = db.rawQuery("PRAGMA table_info(" + CourseContract.CourseEntry.TABLE_NAME + ")", null);
//        while (cursor.moveToNext()) {
//            for(int index = 0; index < cursor.getCount(); index++) {
//                String column = cursor.getString(index);
//                Log.i(AppConstants.APP_TAG, "Column: " + column);//todo try this to see what the columns are
//            }
//        }

        List<CollegeCourse> matchingCourses = new ArrayList<>();
        List<CollegeCourse> allCourses = findAll();

        for (CollegeCourse collegeCourse : allCourses) {
            if (collegeCourse.getDepartmentId() == departmentId) {
                matchingCourses.add(collegeCourse);
            }
        }

        return matchingCourses;
//        String selection = CourseContract.CourseEntry.COLUMN_DEPARTMENT_ID + " = ?";
//        String[] selectionArgs = {String.valueOf(departmentId)};
//
//        return findAllBy(selection, selectionArgs);
    }

    public List<CollegeCourse> findAll() {
        return findAllBy(null, null);
    }

    public boolean insertTestCourses() {
        final List<CollegeCourse> collegeCourses = getTestCollegeCourseList();
        for (CollegeCourse collegeCourse : collegeCourses) {
            if (saveCourse(collegeCourse) == -1) {
                deleteAllCourses();
                return false;
            }
        }

        return true;
    }

    public boolean deleteOrphanedCourses(ArrayList<Integer> removedDepartments) {
        boolean deletedAllCourses = true;
        for (Integer removedDepartmentId : removedDepartments) {
            List<CollegeCourse> coursesToRemove = findAllByDepartment(removedDepartmentId);
            for (CollegeCourse courseToRemove : coursesToRemove) {
                if (!deleteCourse(courseToRemove.getId())) {
                    Log.e(AppConstants.APP_TAG, "Unable to delete course: " + courseToRemove.getId());
                    deletedAllCourses = false;
                }
            }
        }
        return deletedAllCourses;
    }

    public boolean deleteAllCourses() {
        try {
            Log.i(AppConstants.APP_TAG, "Deleting all courses");
            int numberOfRowsDeleted = db.delete(CourseContract.CourseEntry.TABLE_NAME, null, null);
            Log.i(AppConstants.APP_TAG, "CourseDAO deleteAllCourses "
                    + numberOfRowsDeleted + " rows deleted");
            return numberOfRowsDeleted > -1;
        } catch (Exception ex) {
            Log.e(AppConstants.APP_TAG, "Error deleting all courses: " + ex.getMessage());
        }

        return false;
    }

    public boolean deleteCourse(final int id) {
        try {
            int numberOfRowsDeleted = db.delete(CourseContract.CourseEntry.TABLE_NAME,
                    CourseContract.CourseEntry._ID + " = ? ", new String[]{String.valueOf(id)});
            Log.i(AppConstants.APP_TAG, "CourseDAO deleteCourse " + numberOfRowsDeleted
                    + " rows deleted");
            return numberOfRowsDeleted == 1;
        } catch (Exception ex) {
            Log.e(AppConstants.APP_TAG, "Error deleting course id: " + id + " "
                    + ex.getMessage());
        }

        return false;
    }

    private List<CollegeCourse> getTestCollegeCourseList() {
        List<CollegeCourse> collegeCourses = new LinkedList<>();
        collegeCourses.addAll(getTestComputerScienceCourses());
        collegeCourses.addAll(getTestElectricalEngineeringCourses());
        collegeCourses.addAll(getTestInformationTechnologyCourses());
        collegeCourses.addAll(getTestNetworkingCourses());

        return collegeCourses;
    }

    @NonNull
    private List<CollegeCourse> getTestComputerScienceCourses() {
        List<CollegeCourse> collegeCourses = new ArrayList<>();
        int computerScienceDepartmentId = departmentDAO.getComputerScienceDepartmentId();

        if(computerScienceDepartmentId == -1) {
            return new ArrayList<>();
        }

        collegeCourses.add(new CollegeCourse(1, "Mobile App Development", 35, "Durney", "CS 3680", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(2, "Discrete Structures", 25, "Jack", "CS 3681", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(3, "Data Structures", 10, "Smoot", "CS 3682", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(4, "Intro to programming", 45, "Smit", "CS 3683", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(5, "Intro to computers", 30, "Stevens", "CS 3684", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(6, "Programming in Assembly", 35, "Clark", "CS 3685", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(7, "Web Development", 35, "Jeter", "CS 3686", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(8, "Database Theory", 35, "Bryant", "CS 3687", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(9, "MongoDB", 35, "Curry", "CS 3688", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(10, "Linux Administration", 40, "Johnson", "CS 3689", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(11, "Advanced Python", 35, "Smith", "CS 3690", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(12, "Advanced Java", 35, "Benji", "CS 3691", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(13, "Advanced C++", 25, "Durney", "CS 3692", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(14, "Advanced C#", 45, "Matthews", "CS 3693", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(15, "Windows Administration", 35, "Jeffries", "CS 3694", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(16, "Docker", 35, "Sky", "CS 3695", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(17, "Discrete Math", 30, "Max", "CS 3696", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(18, "Big Data", 30, "Griffin", "CS 3697", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(19, "Artificial Intelligence", 35, "Jordan", "CS 3698", computerScienceDepartmentId));
        collegeCourses.add(new CollegeCourse(20, "Compilers", 40, "Jabbar", "CS 3699", computerScienceDepartmentId));
        return collegeCourses;
    }

    @NonNull
    private List<CollegeCourse> getTestNetworkingCourses() {
        List<CollegeCourse> collegeCourses = new ArrayList<>();
        int networkingDepartmentId = departmentDAO.getNetworkingDepartmentId();

        if(networkingDepartmentId == -1) {
            return new ArrayList<>();
        }

        collegeCourses.add(new CollegeCourse(21, "Networks 1", 35, "Durney", "NW 3680", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(22, "Networks 2", 25, "Jack", "NW 3681", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(23, "Networks 3", 10, "Smoot", "NW 3682", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(24, "Network Programming", 45, "Smit", "NW 3683", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(25, "TCP/IP", 30, "Stevens", "NW 3684", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(26, "UDP", 35, "Clark", "NW 3685", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(27, "Sockets", 35, "Jeter", "NW 3686", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(28, "Advanced Networking", 35, "Bryant", "NW 3687", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(29, "Routers", 35, "Curry", "NW 3688", networkingDepartmentId));
        collegeCourses.add(new CollegeCourse(30, "ISP Research", 40, "Johnson", "NW 3689", networkingDepartmentId));
        return collegeCourses;
    }

    @NonNull
    private List<CollegeCourse> getTestInformationTechnologyCourses() {
        List<CollegeCourse> collegeCourses = new ArrayList<>();
        int informationTechnologyDepartmentId = departmentDAO.getInformationTechnologyDepartmentId();

        if(informationTechnologyDepartmentId == -1) {
            return new ArrayList<>();
        }

        collegeCourses.add(new CollegeCourse(31, "IT For Beginners", 35, "Durney", "IT 3680", informationTechnologyDepartmentId));
        collegeCourses.add(new CollegeCourse(32, "Microsoft Office", 25, "Jack", "IT 3681", informationTechnologyDepartmentId));
        collegeCourses.add(new CollegeCourse(33, "Basic Networking", 10, "Smoot", "IT 3682", informationTechnologyDepartmentId));
        collegeCourses.add(new CollegeCourse(34, "Linux", 45, "Smit", "IT 3683", informationTechnologyDepartmentId));
        collegeCourses.add(new CollegeCourse(35, "Advanced Linux", 30, "Stevens", "IT 3684", informationTechnologyDepartmentId));
        collegeCourses.add(new CollegeCourse(36, "Voip", 35, "Clark", "IT 3685", informationTechnologyDepartmentId));
        return collegeCourses;
    }

    @NonNull
    private List<CollegeCourse> getTestElectricalEngineeringCourses() {
        List<CollegeCourse> collegeCourses = new ArrayList<>();
        int electricalEngineeringDepartmentId = departmentDAO.getElectricalEngineeringDepartmentId();

        if(electricalEngineeringDepartmentId == -1) {
            return new ArrayList<>();
        }

        collegeCourses.add(new CollegeCourse(37, "Circuit Boards", 35, "Durney", "EE 3680", electricalEngineeringDepartmentId));
        collegeCourses.add(new CollegeCourse(38, "Household Electronics", 25, "Jack", "EE 3681", electricalEngineeringDepartmentId));
        collegeCourses.add(new CollegeCourse(39, "Commercial Electronics", 10, "Smoot", "EE 3682", electricalEngineeringDepartmentId));
        collegeCourses.add(new CollegeCourse(40, "Automotive Electronics", 45, "Smit", "EE 3683", electricalEngineeringDepartmentId));
        collegeCourses.add(new CollegeCourse(41, "Intro to electricity", 30, "Stevens", "CS 3684", electricalEngineeringDepartmentId));
        return collegeCourses;
    }
}
