package com.jrsqlite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import static com.jrsqlite.AppConstants.DELETED_COURSE;
import static com.jrsqlite.AppConstants.MOVED_COURSE;

public class CourseDetailsActivity extends AppCompatActivity {

    private int courseId;
    private int position;
    private ArrayList<CollegeDepartment> departmentList;
    private ArrayList<String> departmentNames;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        try {
            Bundle extras = getIntent().getExtras();
            String name = extras.getString("name");
            String instructor = extras.getString("instructor");
            int capacity = extras.getInt("capacity");
            String number = extras.getString("number");
            position = extras.getInt("position");
            courseId = extras.getInt("courseId");
            departmentList = getDepartmentList(extras.getStringArrayList("collegeDepartments"));
            departmentNames = getDepartmentNames();

            fillInCourseDetails(name, instructor, capacity, number);
            createSpinner();
            addClickListenerToDeleteCourseButton();
            addClickListenerToMoveCourseButton();
        } catch (Exception ex) {
            Log.e(AppConstants.APP_TAG, "onCreate: There was a problem displaying course details: "
                    + ex.getMessage());
        }
    }

    private void fillInCourseDetails(String name, String instructor, int capacity, String number) {
        TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
        nameTextView.setText("Name: " + name);

        TextView instructorTextView = (TextView) findViewById(R.id.instructor_text_view);
        instructorTextView.setText("Instructor: " + instructor);

        TextView capacityTextView = (TextView) findViewById(R.id.capacity_text_view);
        capacityTextView.setText("Capacity: " + capacity);

        TextView numberTextView = (TextView) findViewById(R.id.number_text_view);
        numberTextView.setText("Number: " + number);
    }

    private void addClickListenerToMoveCourseButton() {
        Button moveCourseButton = (Button) findViewById(R.id.move_course_to_department_button);
        moveCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Object selectedItem = spinner.getSelectedItem();
                if (selectedItem == null) {
                    Toast.makeText(CourseDetailsActivity.this, "Please select a department", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fieldToFilterBy = selectedItem.toString();

                try {
                    Intent intent = getIntent();
                    intent.putExtra("result", MOVED_COURSE);
                    intent.putExtra("newDepartmentId", getDepartmentId(fieldToFilterBy));
                    intent.putExtra("courseIdToMove", courseId);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception ex) {
                    Log.e(AppConstants.APP_TAG, ex.getMessage());
                }

            }
        });
    }

    private void addClickListenerToDeleteCourseButton() {
        Button button = (Button) findViewById(R.id.delete_course_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = getIntent();
                    intent.putExtra("result", DELETED_COURSE);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception ex) {
                    Log.e(AppConstants.APP_TAG, ex.getMessage());
                }
            }
        });
    }

    private int getDepartmentId(String fieldToFilterBy) {
        for (CollegeDepartment collegeDepartment : departmentList) {
            if (collegeDepartment.getName().equalsIgnoreCase(fieldToFilterBy)) {
                return collegeDepartment.getId();
            }
        }

        Log.e(AppConstants.APP_TAG, "Unable to find department: " + fieldToFilterBy);
        return -1;
    }

    private ArrayList<String> getDepartmentNames() {
        ArrayList<String> names = new ArrayList<>();

        for (CollegeDepartment collegeDepartment : departmentList) {
            names.add(collegeDepartment.getName());
        }

        return names;
    }

    private void createSpinner() {
        spinner = (Spinner) findViewById(R.id.move_to_department_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private ArrayList<CollegeDepartment> getDepartmentList(ArrayList<String> collegeDepartmentJsonList) {
        ArrayList<CollegeDepartment> departments = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String departmentJson : collegeDepartmentJsonList) {
            try {
                departments.add(objectMapper.readValue(departmentJson, CollegeDepartment.class));
            } catch (IOException e) {
                Log.e(AppConstants.APP_TAG, "Problem getting department from json: " + e.getMessage());
            }
        }

        return departments;
    }
}
