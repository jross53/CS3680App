package com.jrsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewDepartmentActivity extends AppCompatActivity {

    private final int ADDED_DEPARTMENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_department_activity_details);

        Button button = (Button) findViewById(R.id.add_department_button);//todo I left off here, working on adding a new department. Delete and view is working
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView nameTextView = (TextView) findViewById(R.id.edit_department_name_text_view);
                String name = nameTextView.getText().toString();

                Intent intent = getIntent();
                intent.putExtra("result", ADDED_DEPARTMENT);
                intent.putExtra("name", name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
