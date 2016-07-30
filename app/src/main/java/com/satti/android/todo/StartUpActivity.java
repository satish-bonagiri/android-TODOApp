package com.satti.android.todo;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.satti.android.todo.frag.ToDoListFragment;

public class StartUpActivity extends AppCompatActivity {

    //FrameLayout mFrameLayout;

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private ToDoListFragment mToDoListFragmnet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  mFrameLayout = (FrameLayout)findViewById(R.id.frag_container);

        FragmentManager fragmentManager = getFragmentManager();
        mToDoListFragmnet = (ToDoListFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);

        if(mToDoListFragmnet == null){
            mToDoListFragmnet = new ToDoListFragment();
            fragmentManager.beginTransaction().add(R.id.frag_container,mToDoListFragmnet,TAG_TASK_FRAGMENT).commit();
        }

      //  Log.d("StartUpActivity onCreate END");
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//       // Log.d("StartUpActivity onResume END");
//
//    }
}
