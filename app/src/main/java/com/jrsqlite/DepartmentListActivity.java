package com.jrsqlite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jrsqlite.database.CourseContract;
import com.jrsqlite.database.CourseDAO;

import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;
    private final int DELETED_COURSE = 3;
    private final int ADDED_COURSE = 4;

    private List<CollegeCourse> collegeCourseList;
    private Spinner spinner;
    private CourseDAO courseDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_courses);

        courseDAO = new CourseDAO(getApplicationContext());
        generateNewCollegeCourseListFromDatabase();
        createSpinner();
        createRecyclerView();
        addClickEventToAddNewButton();
        addClickEventToFilterButton();
        addClickEventToClearFilterText();
    }

    private void addClickEventToClearFilterText() {
        TextView clearFilterTextView = (TextView) findViewById(R.id.clear_filter_text_view);
        clearFilterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText filterEditText = (EditText) findViewById(R.id.filter_edit_text);
                filterEditText.setText("");
                collegeCourseList = courseDAO.findAll();
                rvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addClickEventToFilterButton() {
        Button filterButton = (Button) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String fieldToFilterBy = spinner.getSelectedItem().toString().toLowerCase();
                EditText filterEditText = (EditText) findViewById(R.id.filter_edit_text);
                String valueToFilterBy = filterEditText.getText().toString();
                switch (fieldToFilterBy) {
                    case "course name":
                        collegeCourseList = courseDAO.findAllByName(valueToFilterBy);
                        break;
                    case CourseContract.CourseEntry.COLUMN_INSTRUCTOR:
                        collegeCourseList = courseDAO.findAllByInstructor(valueToFilterBy);
                        break;
                    case CourseContract.CourseEntry.COLUMN_NUMBER:
                        collegeCourseList = courseDAO.findAllByNumber(valueToFilterBy);
                        break;
                    case CourseContract.CourseEntry.COLUMN_CAPACITY:
                        int capacityToFilterBy;
                        try {
                            capacityToFilterBy = Integer.parseInt(valueToFilterBy);
                        } catch (NumberFormatException ex) {
                            Toast.makeText(CourseListActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        collegeCourseList = courseDAO.findAllByCapacity(capacityToFilterBy);
                        break;
                    default:
                        Toast.makeText(CourseListActivity.this, "Unknown filter: " + fieldToFilterBy, Toast.LENGTH_SHORT).show();
                        return;
                }
                rvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addClickEventToAddNewButton() {
        ImageButton addNewButton = (ImageButton) findViewById(R.id.add_new_button);
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, NewCourseActivity.class);
                startActivityForResult(intent, ADDED_COURSE);
            }
        });
    }

    private void generateNewCollegeCourseListFromDatabase() {
        List<CollegeCourse> existingCollegeCourses = courseDAO.findAll();
        if(existingCollegeCourses == null || existingCollegeCourses.size() == 0) {
            boolean testCoursesWereInserted = courseDAO.insertTestCourses();
            if (!testCoursesWereInserted) {
                String unableToInsertCoursesMessage = "Unable to insert test courses";
                Log.e("CourseListActivity", unableToInsertCoursesMessage);
                return;
            }
            collegeCourseList = courseDAO.findAll();
        } else {
            collegeCourseList = existingCollegeCourses;
        }



        collegeCourseList = courseDAO.findAll();
    }

    private void createRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new RVAdapter();
        recyclerView.setAdapter(rvAdapter);
    }

    private void createSpinner() {
        spinner = (Spinner) findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemHolder> {

        public class ItemHolder extends RecyclerView.ViewHolder {

            public TextView itemTextView;

            public ItemHolder(View itemView) {
                super(itemView);
                itemTextView = (TextView) itemView;
            }
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CourseListActivity.this, DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    String name = ((AppCompatTextView) v).getText().toString();
                    CollegeCourse collegeCourse = getCollegeCourse(name);
                    if (collegeCourse == null) {
                        Toast.makeText(CourseListActivity.this,
                                "There was a problem finding " + name, Toast.LENGTH_LONG).show();
                        return;
                    }
                    bundle.putString("name", collegeCourse.getName());
                    bundle.putString("instructor", collegeCourse.getInstructor());
                    bundle.putInt("capacity", collegeCourse.getCapacity());
                    bundle.putString("number", collegeCourse.getNumber());
                    int indexOfCourse = getIndexOfCourse(collegeCourse);
                    if (indexOfCourse == -1) {
                        Log.e("CourseListActivity", "Unabled to get index of course: "
                                + collegeCourse.getName());
                        return;
                    }
                    bundle.putInt("position", indexOfCourse);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DELETED_COURSE);
                }
            });

            return new ItemHolder(view);
        }

        private CollegeCourse getCollegeCourse(String name) {
            for (CollegeCourse collegeCourse : collegeCourseList) {
                if (collegeCourse.getName().equals(name)) {
                    return collegeCourse;
                }
            }
            Log.e("CourseListActivity", "Unable to find college course: " + name);
            return null;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            String item = collegeCourseList.get(position).getName();
            holder.itemTextView.setText(item);
        }

        public int getItemCount() {
            return collegeCourseList == null ? 0 : collegeCourseList.size();
        }
    }

    private int getIndexOfCourse(CollegeCourse collegeCourse) {
        for (int index = 0; index < collegeCourseList.size(); index++) {
            CollegeCourse collegeCourseInQuestion = collegeCourseList.get(index);
            if (collegeCourseInQuestion.getName().equals(collegeCourse.getName())
                    && collegeCourseInQuestion.getInstructor().equals(collegeCourse.getInstructor())
                    && collegeCourseInQuestion.getNumber().equals(collegeCourse.getNumber())) {
                return index;
            }
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        int result = data.getIntExtra("result", 9);
        if (result == DELETED_COURSE) {
            int position = data.getIntExtra("position", -1);
            if (position > -1) {
                removeAt(position);
                rvAdapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        } else if (result == ADDED_COURSE) {
            String courseName = data.getStringExtra("name");
            String instructor = data.getStringExtra("instructor");
            int capacity = data.getIntExtra("capacity", 30);
            String courseNumber = data.getStringExtra("number");

            CollegeCourse collegeCourse = new CollegeCourse(courseName, capacity, instructor, courseNumber);
            courseDAO.saveCourse(collegeCourse);
            collegeCourseList.add(0, collegeCourse);
            rvAdapter.notifyItemInserted(0);
            recyclerView.getLayoutManager().scrollToPosition(0);
            Toast.makeText(CourseListActivity.this, "Added " + courseName, Toast.LENGTH_LONG).show();
        }

    }

    public void removeAt(int position) {
        CollegeCourse collegeCourse = collegeCourseList.get(position);
        String name = collegeCourse.getName();
        if(!courseDAO.deleteCourse(collegeCourse)) {
            Toast.makeText(CourseListActivity.this, "Unable to delete course: " + name, Toast.LENGTH_LONG).show();
            return;
        }
        collegeCourseList.remove(position);
        rvAdapter.notifyItemRemoved(position);
        Toast.makeText(CourseListActivity.this, "Removed " + name, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
