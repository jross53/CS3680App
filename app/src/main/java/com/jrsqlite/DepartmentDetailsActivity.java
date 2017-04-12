package com.jrsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DepartmentDetailsActivity extends AppCompatActivity {

    private final int DELETED_COURSE = 3;

    private String name;
    private int id;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        id = extras.getInt("id");
        position = extras.getInt("position");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_details);

        TextView nameTextView = (TextView) findViewById(R.id.department_name_text_view);
        nameTextView.setText("Name: " + name);

        Button button = (Button) findViewById(R.id.delete_department_button);
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
