package com.satti.android.todo.db;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.satti.android.todo.model.Task;
import com.satti.android.todo.provider.ToDoProvider;

import java.util.ArrayList;
import java.util.List;

public class TodoDao {

    private Context mContext;

    public TodoDao(Context mContext) {
        this.mContext = mContext;
    }

    public long insertTask(Task task) {
        long returnVal = -1;
        if (task != null) {
            ContentValues contentValues = new ContentValues();
            populateContentValues(contentValues, task);
            Uri uri = mContext.getContentResolver().insert(ToDoProvider.TODO_CONTENT_URI, contentValues);
            if(uri != null){
                returnVal = ContentUris.parseId(uri);
            }else{
            }
        }
        return returnVal;
    }


    public long updateTask(Task task,String tid){
        long noOFRowsEffected = -1;
        if(task != null && !TextUtils.isEmpty(tid) ){
            ContentValues contentValues = new ContentValues();
            populateContentValues(contentValues, task);

            noOFRowsEffected = mContext.getContentResolver().update(ToDoProvider.TODO_CONTENT_URI, contentValues, SQLiteHelper.TODO_ID + "=?",new String[] {tid});
        }
        return noOFRowsEffected;
    }


    public long deleteTask(Task task,String tid){
        long noOFRowsEffected = -1;
        if(task != null && !TextUtils.isEmpty(tid) ){
            ContentValues contentValues = new ContentValues();
            populateContentValues(contentValues, task);

            noOFRowsEffected = mContext.getContentResolver().delete(ToDoProvider.TODO_CONTENT_URI, SQLiteHelper.TODO_ID + "=?", new String[]{tid});
        }
        return noOFRowsEffected;
    }

    public long deleteTask(String tid){
        long noOFRowsEffected = -1;
        if(!TextUtils.isEmpty(tid) ){
            noOFRowsEffected = mContext.getContentResolver().delete(ToDoProvider.TODO_CONTENT_URI, SQLiteHelper.TODO_ID + "=?", new String[]{tid});
        }
        return noOFRowsEffected;
    }

    private void populateContentValues(ContentValues contentValues,Task task) {
        contentValues.put(SQLiteHelper.TODO_NAME, task.getName());
        contentValues.put(SQLiteHelper.TODO_PRIORITY, task.getPriority());
        contentValues.put(SQLiteHelper.TODO_TIMESTAMP, task.getDateTimeStamp());
    }


    public List<String> getAllTaskNames(String searchText) {
        List<String> mTaskNames = new ArrayList<String>();
        //Cursor mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null, null, null, null);
        //Cursor mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,SQLiteHelper.TODO_NAME  +  " LIKE '%"+searchText+"%'" , null, null);
        Cursor mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, new String[]{SQLiteHelper.TODO_NAME},SQLiteHelper.TODO_NAME + " LIKE ?", new String[] {"%" + searchText + "%"} , null);

        if (mCursor != null && mTaskNames != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                String taskName = mCursor.getString(0);
                mTaskNames.add(taskName);
                mCursor.moveToNext();
            }
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return mTaskNames;
    }

    public List<Task> getAllTasks() {
        List<Task> mTasks = new ArrayList<Task>();
        Cursor mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ," rowId DESC");

        if (mCursor != null && mTasks != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                Task task = new Task();
                populateTaskFromCursor(mCursor, task);
                mTasks.add(task);
                mCursor.moveToNext();
            }
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return mTasks;
    }

    private void populateTaskFromCursor(Cursor mCursor, Task mTask) {
        mTask.setId(mCursor.getLong(mCursor.getColumnIndex(SQLiteHelper.TODO_ID)));
        mTask.setName(mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.TODO_NAME)));
        mTask.setPriority(mCursor.getInt(mCursor.getColumnIndex(SQLiteHelper.TODO_PRIORITY)));
        mTask.setDateTimeStamp(mCursor.getLong(mCursor.getColumnIndex(SQLiteHelper.TODO_TIMESTAMP)));
    }


    public List<Task> getAllTasks(String searchText) {
        List<Task> mTasks = new ArrayList<Task>();
        Cursor mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,SQLiteHelper.TODO_NAME + " LIKE ?", new String[] {"%" + searchText + "%"} , null);

        if (mCursor != null && mTasks != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                Task task = new Task();
                populateTaskFromCursor(mCursor, task);
                mTasks.add(task);
                mCursor.moveToNext();
            }
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return mTasks;
    }

    public List<Task> getAllTasksByCriteria(String sortCriteria) {
        if(sortCriteria == null){
            return  null;
        }
        List<Task> mTasks = new ArrayList<Task>();
        Cursor mCursor = null;
        if(sortCriteria.contains("date")){
            mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ,SQLiteHelper.TODO_TIMESTAMP +" DESC");
        }else if(sortCriteria.contains("priority")){
            mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ,SQLiteHelper.TODO_PRIORITY +" DESC");
        }else if(sortCriteria.contains("name")){
            mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ,SQLiteHelper.TODO_NAME +" ASC");
        }

        if (mCursor != null && mTasks != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                Task task = new Task();
                populateTaskFromCursor(mCursor, task);
                mTasks.add(task);
                mCursor.moveToNext();
            }
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

        return mTasks;
    }

    public Task getTaskById(long tid){

        Task task = new Task();
        Cursor mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,SQLiteHelper.TODO_ID + "=?", new String[] {Long.toString(tid)} ,null);

        if (mCursor != null && task != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                populateTaskFromCursor(mCursor, task);
                mCursor.moveToNext();
            }
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return task;
    }

    public Cursor getAllTaskCursor(){
        return mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ," rowId DESC");
    }

    public Cursor getAllTasksByCriteriaCursor(String sortCriteria){
        Cursor mCursor = null;
        if(sortCriteria == null){
            mCursor=  mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ," rowId DESC");
        }else if(sortCriteria.contains("date")){
            mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ,SQLiteHelper.TODO_TIMESTAMP +" DESC");
        }else if(sortCriteria.contains("priority")){
            mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ,SQLiteHelper.TODO_PRIORITY +" DESC");
        }else if(sortCriteria.contains("name")){
            mCursor = mContext.getContentResolver().query(ToDoProvider.TODO_CONTENT_URI, null,null, null ,SQLiteHelper.TODO_NAME +" ASC");
        }
        return mCursor;
    }
}
