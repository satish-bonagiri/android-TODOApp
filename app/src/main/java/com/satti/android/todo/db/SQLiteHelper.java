package com.satti.android.todo.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{

    public static final String TAG = "SQLiteHelper";

    private static SQLiteHelper mDataBaseHelper = null;
    public static final String DATABASE_NAME = "todoapp.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TODO_TABLE_NAME = "todo";

    public static final String TODO_ID ="_tId";
    public static final String TODO_NAME = "tName";
    public static final String TODO_PRIORITY = "tPriority";
    public static final String TODO_TIMESTAMP = "timeStamp";

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteHelper getSQLiteHelper(Context mContext){
        if(mDataBaseHelper == null){
            mDataBaseHelper = new SQLiteHelper(mContext);
        }
        return mDataBaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateToDoTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    private String getCreateToDoTableString(){

        StringBuffer mStringBuffer = new StringBuffer();
        mStringBuffer.append("create table ");
        mStringBuffer.append(TODO_TABLE_NAME);
        mStringBuffer.append("( ");
        mStringBuffer.append(TODO_ID);
        mStringBuffer.append(" integer primary key not null,");
        mStringBuffer.append(TODO_NAME);
        mStringBuffer.append(" text not null,");
        mStringBuffer.append(TODO_PRIORITY);
        mStringBuffer.append(" integer not null,");
        mStringBuffer.append(TODO_TIMESTAMP);
        mStringBuffer.append(" integer not null");
        mStringBuffer.append(");");
        return mStringBuffer.toString();
    }
}
