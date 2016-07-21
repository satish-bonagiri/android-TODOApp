package com.satti.android.todo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.satti.android.todo.adapter.CustomArrayAdapter;
import com.satti.android.todo.db.TodoDao;
import com.satti.android.todo.model.Task;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private EditText mSearchEditText;
    private ListView mSearchListView;
    private TodoDao mTodoDao;
    CustomArrayAdapter mArrayAdapter;
    private List<Task> mSearchTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        mSearchEditText = (EditText)findViewById(R.id.search_edittext);
        mSearchListView = (ListView)findViewById(R.id.search_listview);
        mTodoDao = new TodoDao(getApplicationContext());
        mSearchEditText.addTextChangedListener(textWatcher);
//        mSearchEditText.setOnEditorActionListener(onEditorActionListener);
        mSearchTaskList = new ArrayList<Task>();
        mArrayAdapter = new CustomArrayAdapter(SearchActivity.this,R.layout.listitem, mSearchTaskList,true);
        mSearchListView.setAdapter(mArrayAdapter);
        mArrayAdapter.notifyDataSetChanged();

    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            String searchText = mSearchEditText.getText().toString();
            String trimmedSearchText = searchText.trim();
            if (trimmedSearchText.length() >= 1) {
                //FIXME move this db call to back ground,to avoid ANR in low end devices !
                List<Task> mTempList = mTodoDao.getAllTasks(trimmedSearchText);
                if(mTempList != null && mTempList.size() > 0){
                    mSearchTaskList.clear(); //clear the old data
                    //Log.d("onTextChanged::: " + mTempList.toString());
                    for(int i=0 ; i < mTempList.size() ;i++){
                        mSearchTaskList.add(mTempList.get(i));
                    }
                    mArrayAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

//    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                //Do the manual/free search
//                Toast.makeText(SearchActivity.this,"Do Free Text !!!",Toast.LENGTH_SHORT).show();
//            }
//            return false;
//        }
//    };
}
