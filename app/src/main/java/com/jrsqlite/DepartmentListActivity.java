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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jrsqlite.database.DepartmentDAO;

import java.util.List;

public class DepartmentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DepartmentAdapter departmentAdapter;
    private final int DELETED_DEPARTMENT = 3;
    private final int ADDED_DEPARTMENT = 4;

    private List<CollegeDepartment> collegeDepartmentList;
    private DepartmentDAO departmentDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_departments);

        departmentDAO = new DepartmentDAO(getApplicationContext());
        generateNewCollegeDepartmentListFromDatabase();
        createRecyclerView();
        addClickEventToAddNewButton();
        addClickEventToFilterButton();
        addClickEventToClearFilterText();
    }

    private void addClickEventToClearFilterText() {
        TextView clearFilterTextView = (TextView) findViewById(R.id.clear_department_filter_text_view);
        clearFilterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText filterEditText = (EditText) findViewById(R.id.department_filter_edit_text);
                filterEditText.setText("");
                collegeDepartmentList = departmentDAO.findAll();
                departmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addClickEventToFilterButton() {
        Button filterButton = (Button) findViewById(R.id.department_search_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                EditText filterEditText = (EditText) findViewById(R.id.department_filter_edit_text);
                String valueToFilterBy = filterEditText.getText().toString();
                collegeDepartmentList = departmentDAO.findAllByName(valueToFilterBy);
                departmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addClickEventToAddNewButton() {
        ImageButton addNewButton = (ImageButton) findViewById(R.id.add_new_department_button);
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepartmentListActivity.this, NewDepartmentActivity.class);
                startActivityForResult(intent, ADDED_DEPARTMENT);
            }
        });
    }

    private void generateNewCollegeDepartmentListFromDatabase() {
        List<CollegeDepartment> existingCollegeDepartments = departmentDAO.findAll();
        if(existingCollegeDepartments == null || existingCollegeDepartments.size() == 0) {
            boolean testDepartmentsWereInserted = departmentDAO.insertTestDepartments();
            if (!testDepartmentsWereInserted) {
                String unableToInsertDepartmentsMessage = "Unable to insert test departments";
                Log.e("DepartmentListActivity", unableToInsertDepartmentsMessage);
                return;
            }
            collegeDepartmentList = departmentDAO.findAll();
        } else {
            collegeDepartmentList = existingCollegeDepartments;
        }



        collegeDepartmentList = departmentDAO.findAll();
    }

    private void createRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.department_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        departmentAdapter = new DepartmentAdapter();
        recyclerView.setAdapter(departmentAdapter);
    }

    public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ItemHolder> {

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
                    Intent intent = new Intent(DepartmentListActivity.this, DepartmentDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    String name = ((AppCompatTextView) v).getText().toString();
                    CollegeDepartment collegeDepartment = getCollegeDepartment(name);
                    if (collegeDepartment == null) {
                        Toast.makeText(DepartmentListActivity.this,
                                "There was a problem finding " + name, Toast.LENGTH_LONG).show();
                        return;
                    }
                    bundle.putString("name", collegeDepartment.getName());
                    int indexOfDepartment = getIndexOfDepartment(collegeDepartment);
                    if (indexOfDepartment == -1) {
                        Log.e("DepartmentListActivity", "Unable to get index of department: "
                                + collegeDepartment.getName());
                        return;
                    }
                    bundle.putInt("position", indexOfDepartment);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DELETED_DEPARTMENT);
                }
            });

            return new ItemHolder(view);
        }

        private CollegeDepartment getCollegeDepartment(String name) {
            for (CollegeDepartment collegeDepartment : collegeDepartmentList) {
                if (collegeDepartment.getName().equals(name)) {
                    return collegeDepartment;
                }
            }
            Log.e("DepartmentListActivity", "Unable to find college department: " + name);
            return null;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            String item = collegeDepartmentList.get(position).getName();
            holder.itemTextView.setText(item);
        }

        public int getItemCount() {
            return collegeDepartmentList == null ? 0 : collegeDepartmentList.size();
        }
    }

    private int getIndexOfDepartment(CollegeDepartment collegeDepartment) {
        for (int index = 0; index < collegeDepartmentList.size(); index++) {
            CollegeDepartment collegeDepartmentInQuestion = collegeDepartmentList.get(index);
            if (collegeDepartmentInQuestion.getName().equals(collegeDepartment.getName())) {
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
        if (result == DELETED_DEPARTMENT) {
            int position = data.getIntExtra("position", -1);
            if (position > -1) {
                removeAt(position);
                departmentAdapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        } else if (result == ADDED_DEPARTMENT) {
            String departmentName = data.getStringExtra("name");

            CollegeDepartment collegeDepartment = new CollegeDepartment(departmentName);
            departmentDAO.saveDepartment(collegeDepartment);
            collegeDepartmentList.add(0, collegeDepartment);
            departmentAdapter.notifyItemInserted(0);
            recyclerView.getLayoutManager().scrollToPosition(0);
            Toast.makeText(DepartmentListActivity.this, "Added " + departmentName, Toast.LENGTH_LONG).show();
        }

    }

    public void removeAt(int position) {
        CollegeDepartment collegeDepartment = collegeDepartmentList.get(position);
        String name = collegeDepartment.getName();
        if(!departmentDAO.deleteDepartment(collegeDepartment)) {
            Toast.makeText(DepartmentListActivity.this, "Unable to delete department: " + name, Toast.LENGTH_LONG).show();
            return;
        }
        collegeDepartmentList.remove(position);
        departmentAdapter.notifyItemRemoved(position);
        Toast.makeText(DepartmentListActivity.this, "Removed " + name, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
