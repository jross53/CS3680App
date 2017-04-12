package com.jrsqlite;


/**
 * Created by Jordan.Ross on 2/11/2017.
 */
public class CollegeCourse {
    private String name;
    private int capacity;
    private String instructor;
    private String number;

    public CollegeCourse(String name, int capacity, String instructor, String number) {
        this.name = name;
        this.capacity = capacity;
        this.instructor = instructor;
        this.number = number;
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
}
