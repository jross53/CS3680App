package com.jrsqlite.database;

import android.provider.BaseColumns;

/**
 * Created by Jordan.Ross on 2/25/2017.
 */

public final class CourseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CourseContract() {}

    /* Inner class that defines the table contents */
    public static class CourseEntry implements BaseColumns {
        public static final String TABLE_NAME = "course";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INSTRUCTOR = "instructor";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_CAPACITY = "capacity";
        public static final String COLUMN_DEPARTMENT_ID = "departmentId";
    }
}
