package com.jrsqlite.database;

import android.provider.BaseColumns;

/**
 * Created by Jordan.Ross on 2/25/2017.
 */

public final class DepartmentContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DepartmentContract() {}

    public static class DepartmentEntry implements BaseColumns {
        public static final String TABLE_NAME = "department";
        public static final String COLUMN_NAME = "name";
        public static final String DEPARTMENT_ID = "departmentId";
    }
}
