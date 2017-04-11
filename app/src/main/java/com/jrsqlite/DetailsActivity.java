package com.jrsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private final int DELETED_COURSE = 3;

    private String name;
    private String instructor;
    private int capacity;
    private String number;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        instructor = extras.getString("instructor");
        capacity = extras.getInt("capacity");
        number = extras.getString("number");
        position = extras.getInt("position");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
        nameTextView.setText("Name: " + name);

        TextView instructorTextView = (TextView) findViewById(R.id.instructor_text_view);
        instructorTextView.setText("Instructor: " + instructor);

        TextView capacityTextView = (TextView) findViewById(R.id.capacity_text_view);
        capacityTextView.setText("Capacity: " + capacity);

        TextView numberTextView = (TextView) findViewById(R.id.number_text_view);
        numberTextView.setText("Number: " + number);

        Button button = (Button) findViewById(R.id.delete_course_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("result", DELETED_COURSE);
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
