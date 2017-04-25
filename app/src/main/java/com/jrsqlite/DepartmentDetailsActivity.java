package com.jrsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jrsqlite.database.DepartmentConstants;

import java.util.ArrayList;

public class DepartmentDetailsActivity extends AppCompatActivity {

    private final int DELETED_COURSE = 3;

    private String name;
    private int id;
    private int position;
    private ArrayList<String> departmentJsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        id = extras.getInt("id");
        position = extras.getInt("position");
        departmentJsonList = extras.getStringArrayList("collegeDepartments");
        final ArrayList<Integer> removedDepartments = extras.getIntegerArrayList("removedDepartments");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_details);

        TextView nameTextView = (TextView) findViewById(R.id.department_name_text_view);
        nameTextView.setText("Name: " + name);

        Button deleteDepartmentButton = (Button) findViewById(R.id.delete_department_button);
        deleteDepartmentButton.setOnClickListener(new View.OnClickListener() {
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

        Button viewCoursesButton = (Button) findViewById(R.id.view_courses_button);
        viewCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt(DepartmentConstants.CURRENT_DEPARTMENT_ID, id);
                    bundle.putIntegerArrayList("removedDepartments", removedDepartments);
                    bundle.putStringArrayList("collegeDepartments", departmentJsonList);

                    Intent intent = new Intent(DepartmentDetailsActivity.this, CourseListActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch(Exception ex) {
                    Log.e(AppConstants.APP_TAG, ex.getMessage());
                }
            }
        });
    }
}
