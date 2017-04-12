package com.jrsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewCourseActivity extends AppCompatActivity {

    private final int ADDED_COURSE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_course_activity_details);

//        TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
//        nameTextView.setText("Name: " + name);

//        TextView instructorTextView = (TextView) findViewById(R.id.instructor_text_view);
//        instructorTextView.setText("Instructor: " + instructor);

//        TextView capacityTextView = (TextView) findViewById(R.id.capacity_text_view);
//        capacityTextView.setText("Capacity: " + capacity);

//        TextView numberTextView = (TextView) findViewById(R.id.number_text_view);
//        numberTextView.setText("Number: " + number);

        Button button = (Button) findViewById(R.id.add_course_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView nameTextView = (TextView) findViewById(R.id.edit_name_text_view);
                String name = nameTextView.getText().toString();

                TextView instructorTextView = (TextView) findViewById(R.id.new_instructor_edit_text_view);
                String instructor = instructorTextView.getText().toString();

                TextView numberTextView = (TextView) findViewById(R.id.new_number_edit_view);
                String number = numberTextView.getText().toString();

                TextView capacityTextView = (TextView) findViewById(R.id.new_capacity_edit_text_view);
                int capacity = Integer.parseInt(capacityTextView.getText().toString());

                Intent intent = getIntent();
                intent.putExtra("result", ADDED_COURSE);
                intent.putExtra("name", name);
                intent.putExtra("capacity", capacity);
                intent.putExtra("instructor", instructor);
                intent.putExtra("number", number);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
