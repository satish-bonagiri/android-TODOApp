package com.satti.android.todo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.satti.android.todo.adapter.CustomArrayAdapter;
import com.satti.android.todo.db.TodoDao;
import com.satti.android.todo.model.Task;
import com.satti.android.todo.util.ProgressUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StartUpActivity extends AppCompatActivity {

    //FIXME refactor all this code to a fragment !
    private ImageView mFabAddButton;
    private RetrieveAllTasks mRetrieveAllTasks;
    CustomArrayAdapter mArrayAdapter;
    ListView mListView;
    List<Task> mTaskList;
    TodoDao mTodoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.task_listview);
        mTodoDao = new TodoDao(getApplicationContext());
        mTaskList = new ArrayList<>();
        mArrayAdapter = new CustomArrayAdapter(StartUpActivity.this,R.layout.listitem, mTaskList,false);
        mListView.setEmptyView(findViewById(R.id.list_empty_view));
        mListView.setAdapter(mArrayAdapter);
        mArrayAdapter.notifyDataSetChanged();
        mFabAddButton = (ImageView)findViewById(R.id.fab_add);
        mFabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(StartUpActivity.this,AddOrEditATaskActivity.class);
                addTaskIntent.putExtra("isFromAdd",true);
                startActivity(addTaskIntent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mRetrieveAllTasks == null){
            mRetrieveAllTasks = new RetrieveAllTasks();
            mRetrieveAllTasks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sort_by_date:
                handleSortOPtions("date");
                return true;
            case R.id.menu_sort_by_name:
                handleSortOPtions("name");
                return true;
            case R.id.menu_sort_by_priority:
                handleSortOPtions("priority");
                return true;
            case R.id.menu_search:
                Intent searchIntent = new Intent(StartUpActivity.this,SearchActivity.class);
                startActivity(searchIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO ,move this code to background,to avoid ANR in low end devices !
    public void handleSortOPtions(String sortCriteria){
        List<Task> mTempList = mTodoDao.getAllTasksByCriteria(sortCriteria);
        if(mTempList != null && mTempList.size() > 0){
            mTaskList.clear(); //clear the old data
            for(int i=0 ; i < mTempList.size() ;i++){
                mTaskList.add(mTempList.get(i));
            }
            mArrayAdapter.notifyDataSetChanged();
        }
    }

    private class RetrieveAllTasks extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressUtil.displayProgressDialog(StartUpActivity.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mTaskList != null){
                mTaskList.clear(); //make sure , remove any old data
            }
            List<Task> mTempTaskList = mTodoDao.getAllTasks();
            for(int i=0 ; i < mTempTaskList.size() ;i++){
                mTaskList.add(mTempTaskList.get(i));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ProgressUtil.hideProgressDialog();
            mRetrieveAllTasks = null;//to make sure to reload the data again !!!


            if(mArrayAdapter != null){
                mArrayAdapter.notifyDataSetChanged();
            }
//            if(mListView != null){
//                mListView.setSelection(0);
//            }
        }
    }


    @Override
    protected void onDestroy() {
        if(mRetrieveAllTasks != null){
            mRetrieveAllTasks.cancel(true);
            mRetrieveAllTasks = null;
        }
        super.onDestroy();
    }
}
