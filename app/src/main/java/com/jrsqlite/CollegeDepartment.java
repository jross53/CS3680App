package com.jrsqlite;


/**
 * Created by Jordan.Ross on 2/11/2017.
 */
public class CollegeDepartment {
    private int id;
    private String name;

    public CollegeDepartment(String name) {
        this.name = name;
    }

    public CollegeDepartment(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
}
