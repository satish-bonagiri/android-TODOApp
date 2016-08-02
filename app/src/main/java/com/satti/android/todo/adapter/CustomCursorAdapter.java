package com.satti.android.todo.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.satti.android.todo.AddOrEditATaskActivity;
import com.satti.android.todo.R;
import com.satti.android.todo.StartUpActivity;
import com.satti.android.todo.db.SQLiteHelper;
import com.satti.android.todo.db.TodoDao;
import com.satti.android.todo.frag.ToDoListFragment;
import com.satti.android.todo.model.Task;
import com.satti.android.todo.provider.ToDoProvider;
import com.satti.android.todo.util.DateTimeUtil;

/**
 * Created by satish on 31/07/16.
 */
public class CustomCursorAdapter extends CursorAdapter {


    private String[] mPriorityStrings;
    private boolean isFromSearch;
    private Context mContext;
    private TodoDao mTodoDao;


    public CustomCursorAdapter(Context context, Cursor c, int flags,boolean isFromSearch) {
        super(context, c, flags);
        mPriorityStrings = context.getResources().getStringArray(R.array.priority_array);
        this.isFromSearch = isFromSearch;
        mContext = context;
        if(mTodoDao == null){
            mTodoDao = new TodoDao(mContext.getApplicationContext());
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView =  LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mTaskNameTextView = (TextView) rowView.findViewById(R.id.name_textview);
        viewHolder.mPriorityTextView = (TextView) rowView.findViewById(R.id.priority_textview);
        viewHolder.mDateTextView = (TextView) rowView.findViewById(R.id.date_textview);
        viewHolder.mEditImageView = (ImageView) rowView.findViewById(R.id.edit_imageview);
        viewHolder.mDeleteImageView = (ImageView) rowView.findViewById(R.id.delete_imageview);
        rowView.setTag(viewHolder);
        return  rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.mTaskNameTextView.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.TODO_NAME)));
        int taskPriority = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TODO_PRIORITY));
        if(taskPriority != -1){
            viewHolder.mPriorityTextView.setText(mPriorityStrings[taskPriority]);
        }

        if(isFromSearch){
            viewHolder.mEditImageView.setVisibility(View.INVISIBLE);
            viewHolder.mDeleteImageView.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.mEditImageView.setVisibility(View.VISIBLE);
            viewHolder.mEditImageView.setTag(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TODO_ID)));
            viewHolder.mEditImageView.setOnClickListener(onClickListener);
            viewHolder.mDeleteImageView.setVisibility(View.VISIBLE);
            viewHolder.mDeleteImageView.setTag(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TODO_ID)));
            viewHolder.mDeleteImageView.setOnClickListener(onClickListener);
        }
        viewHolder.mDateTextView.setText(DateTimeUtil.getFormattedDate(cursor.getLong(cursor.getColumnIndex(SQLiteHelper.TODO_TIMESTAMP))));

    }

    static class ViewHolder{
        TextView mTaskNameTextView;
        TextView mPriorityTextView;
        TextView mDateTextView;
        ImageView mEditImageView;
        ImageView mDeleteImageView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ImageView imageView = (ImageView)view;
            Integer _id = (Integer) imageView.getTag();
            switch (imageView.getId()){
                case R.id.edit_imageview:
                    Intent editIntent = new Intent(mContext, AddOrEditATaskActivity.class);
                    editIntent.putExtra("taskId",_id);
                    editIntent.putExtra("isFromAdd", false); //call for edit
                    ((StartUpActivity)mContext).startActivityForResult(editIntent, ToDoListFragment.REQUEST_FOR_EDIT);
                    break;
                case R.id.delete_imageview:
                    long retVal  = mTodoDao.deleteTask(Long.toString(_id));
                    if(retVal >= 0){
                        Toast.makeText(mContext,R.string.task_delete_sucess,Toast.LENGTH_SHORT).show();
                    }
                    //FIXME do it in background
                    Cursor mCursor = mTodoDao.getAllTasksByCriteriaCursor(null);
                    swapCursor(mCursor);
                    notifyDataSetChanged();
                    break;
            }
        }
    };
}
