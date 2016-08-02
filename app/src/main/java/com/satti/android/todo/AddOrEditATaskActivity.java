package com.satti.android.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.satti.android.todo.db.TodoDao;
import com.satti.android.todo.model.Task;
import com.satti.android.todo.util.DateTimeUtil;

import java.util.Calendar;


public class AddOrEditATaskActivity extends AppCompatActivity {


    private EditText mTaskEditText;
    private EditText mDateEditText;
    private Spinner mPrioritySpinner;
    private Button mSaveButton;
    private Button mEditButton;

    private String mTaskName = "";
    private int mPriority = 0;
    private String mDateString = "";
    ArrayAdapter<CharSequence> mSpinnerAdapter;

//    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private Bundle mBundle;
    private boolean isFromAdd;
    private Task mTask;
    private long mTaskID;
    private TodoDao mTodoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_task_layout);
        initViews();
        if(mTodoDao == null){
            mTodoDao = new TodoDao(getApplicationContext());
        }
        mBundle = getIntent().getExtras();
        isFromAdd = mBundle.getBoolean("isFromAdd");
        if(isFromAdd){ //from add flow
            setTitle(R.string.add_a_task);
            mSaveButton.setVisibility(View.VISIBLE);
            mEditButton.setVisibility(View.GONE);
            setCurrentDate(-1);
        }else{ //from edit flow
            setTitle(R.string.edit_a_task);
            mSaveButton.setVisibility(View.GONE);
            mEditButton.setVisibility(View.VISIBLE);
            mTaskID = (int) mBundle.get("taskId");
            if(mTaskID != -1){
                //TODO move this db code to background
                Task task = mTodoDao.getTaskById(mTaskID);
                if(task != null){
                    mTaskName = task.getName();
                    mTaskEditText.setText(mTaskName);
                    mPriority = task.getPriority();
                    mPrioritySpinner.setSelection(mPriority);
                    mDateString = DateTimeUtil.getFormattedDate(task.getDateTimeStamp());
                    mDateEditText.setText(mDateString);
                    setCurrentDate(task.getDateTimeStamp());
                }
            }
        }
    }

    private void initViews(){
        mTaskEditText = (EditText)findViewById(R.id.task_edittext);
        mDateEditText = (EditText)findViewById(R.id.date_edittext);
        mPrioritySpinner = (Spinner)findViewById(R.id.task_priority_spinner);
        mSaveButton = (Button)findViewById(R.id.btn_addTask);
        mEditButton = (Button)findViewById(R.id.btn_editTask);

        mSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.priority_array, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPrioritySpinner.setAdapter(mSpinnerAdapter);
        mPrioritySpinner.setOnItemSelectedListener(onItemSelectedListener);
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new DatePickerDialog(AddOrEditATaskActivity.this, myDateListener, year, month, day);
                dialog.show();
            }
        });
    }

    public void onSaveBtnClick(View view){
        TodoDao todoDao = new TodoDao(getApplicationContext());
        if(isAllDataAvailableToInsert()){
            Task task = new Task(mTaskName,mPriority, DateTimeUtil.getTimeStamp(mDateString));
            long retVal = todoDao.insertTask(task);
            if(retVal >= 0){
                Toast.makeText(AddOrEditATaskActivity.this,R.string.task_save_sucess,Toast.LENGTH_SHORT).show();
            }
            //resetViews();
            setResult(RESULT_OK);
            finish();
        }
    }

    public void onEditBtnClick(View view){
        TodoDao todoDao = new TodoDao(getApplicationContext());
        if(isAllDataAvailableToInsert()){
            Task task = new Task(mTaskName,mPriority, DateTimeUtil.getTimeStamp(mDateString));
            long retVal = todoDao.updateTask(task,Long.toString(mTaskID));
            if(retVal >= 0){
                Toast.makeText(AddOrEditATaskActivity.this,R.string.task_edit_sucess,Toast.LENGTH_SHORT).show();
            }
            //resetViews();
            setResult(RESULT_OK);
            finish();
        }
    }



    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                mPriority = pos;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    public void resetViews(){
        if(mPrioritySpinner != null){
            mPrioritySpinner.setSelection(0);
        }
        setCurrentDate(-1);
        if(mTaskEditText != null){
            mTaskEditText.setText("");
        }
    }

    public void setCurrentDate(long timeInMillis){
        calendar = Calendar.getInstance();
        if(timeInMillis != -1){
            calendar.setTimeInMillis(timeInMillis);
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }

    private void showDate(int year, int month, int day) {
        if(mDateEditText != null){
            mDateEditText.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            showDate(year, month+1, day);
        }
    };

    private boolean isAllDataAvailableToInsert(){
        boolean retVal = true;
        if(!TextUtils.isEmpty(mTaskEditText.getText())){
            mTaskName = mTaskEditText.getText().toString();
        }else{
            Toast.makeText(AddOrEditATaskActivity.this,R.string.error_taskname,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mDateEditText != null){
            mDateString = mDateEditText.getText().toString();
        }
        return  retVal;
    }

}
