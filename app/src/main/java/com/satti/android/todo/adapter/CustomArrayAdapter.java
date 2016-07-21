package com.satti.android.todo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.satti.android.todo.AddOrEditATaskActivity;
import com.satti.android.todo.R;
import com.satti.android.todo.db.TodoDao;
import com.satti.android.todo.model.Task;
import com.satti.android.todo.util.DateTimeUtil;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Task> {
	
	private Context mContext;
	private List<Task> mList;
	private LayoutInflater inflater;
	private String[] mPriorityStrings;
	private boolean isFromSearch;
	TodoDao mTodoDao;

	public CustomArrayAdapter(Context context, int textViewResourceId,List<Task> objects,boolean isFromSearch) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
		this.mList = objects;
		this.inflater =(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPriorityStrings = mContext.getResources().getStringArray(R.array.priority_array);
		this.isFromSearch = isFromSearch;
		mTodoDao = new TodoDao(mContext.getApplicationContext());
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}
	
	@Override
	public Task getItem(int position) {
		return mList.get(position);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Task task = getItem(position);
		ViewHolder viewHolder;

		if(convertView == null){
			convertView = inflater.inflate(R.layout.listitem,null);
			viewHolder = new ViewHolder();
			viewHolder.mTaskNameTextView = (TextView) convertView.findViewById(R.id.name_textview);
			viewHolder.mPriorityTextView = (TextView) convertView.findViewById(R.id.priority_textview);
			viewHolder.mDateTextView = (TextView) convertView.findViewById(R.id.date_textview);
			viewHolder.mEditImageView = (ImageView) convertView.findViewById(R.id.edit_imageview);
			viewHolder.mDeleteImageView = (ImageView) convertView.findViewById(R.id.delete_imageview);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mTaskNameTextView.setText(task.getName());
		if(task.getPriority() != -1){
			viewHolder.mPriorityTextView.setText(mPriorityStrings[task.getPriority()]);
		}

		if(isFromSearch){
			viewHolder.mEditImageView.setVisibility(View.INVISIBLE);
			viewHolder.mDeleteImageView.setVisibility(View.INVISIBLE);
		}else{
			viewHolder.mEditImageView.setVisibility(View.VISIBLE);
			viewHolder.mEditImageView.setTag(position);
			viewHolder.mEditImageView.setOnClickListener(onClickListener);
			viewHolder.mDeleteImageView.setVisibility(View.VISIBLE);
			viewHolder.mDeleteImageView.setTag(position);
			viewHolder.mDeleteImageView.setOnClickListener(onClickListener);
		}
		viewHolder.mDateTextView.setText(DateTimeUtil.getFormattedDate(task.getDateTimeStamp()));
		return convertView;
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
			Task task = mList.get(_id);
			switch (imageView.getId()){
				case R.id.edit_imageview:
					Intent editIntent = new Intent(mContext, AddOrEditATaskActivity.class);
					editIntent.putExtra("task",task);
					editIntent.putExtra("isFromAdd", false); //call for edit
					mContext.startActivity(editIntent);
					break;
				case R.id.delete_imageview:
					if(mTodoDao == null){
						mTodoDao = new TodoDao(mContext.getApplicationContext());
					}
					long retVal  = mTodoDao.deleteTask(task,Long.toString(task.getId()));
					if(retVal >= 0){
						mList.remove(task);
						Toast.makeText(mContext,R.string.task_delete_sucess,Toast.LENGTH_SHORT).show();
					}
					notifyDataSetChanged();
					break;
			}
		}
	};
}
