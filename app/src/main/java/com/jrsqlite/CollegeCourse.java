package com.jrsqlite;


/**
 * Created by Jordan.Ross on 2/11/2017.
 */
public class CollegeCourse {
    private int id;
    private String name;
    private int capacity;
    private String instructor;
    private String number;
    private int departmentId;

    public CollegeCourse(int id, String name, int capacity, String instructor, String number, int departmentId) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.instructor = instructor;
        this.number = number;
        this.departmentId = departmentId;
    }

    public CollegeCourse(String name, int capacity, String instructor, String number, int departmentId) {
        this.name = name;
        this.capacity = capacity;
        this.instructor = instructor;
        this.number = number;
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
