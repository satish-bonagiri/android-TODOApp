package com.satti.android.todo.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.satti.android.todo.db.SQLiteHelper;

public class ToDoProvider extends ContentProvider{

    private SQLiteHelper mSqLiteHelper;
    private static final UriMatcher sUriMatcher;

    public static final String AUTHORITY = "com.satti.android.todo.ToDoProvider";
    public static final String CONTENT_TYPE = "vnd.pmd.cursor.dir/vnd.pmd.todos";
    public static final String CONTENT_ITEM_TYPE = "vnd.pmd.cursor.item/vnd.pmd.todo";

    public static final Uri TODO_CONTENT_URI = Uri.parse("content://"+AUTHORITY +  "/" + SQLiteHelper.TODO_TABLE_NAME);

    private static final int TODOS = 1;
    private static final int TODO_ID = 2;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SQLiteHelper.TODO_TABLE_NAME, TODOS);
        sUriMatcher.addURI(AUTHORITY, SQLiteHelper.TODO_TABLE_NAME + "/#", TODO_ID);

    }

    @Override
    public boolean onCreate() {
        mSqLiteHelper = SQLiteHelper.getSQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TODOS:
                return CONTENT_TYPE;
            case TODO_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor mCursor = null ;
        switch (sUriMatcher.match(uri)) {
            case TODOS:
                qb.setTables(SQLiteHelper.TODO_TABLE_NAME);
                SQLiteDatabase db = mSqLiteHelper.getReadableDatabase();
                mCursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                mCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return mCursor;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(SQLiteHelper.TODO_TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        if (rowID >= 0) {
            Uri insertUri = ContentUris.withAppendedId(TODO_CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(insertUri, null);
            return insertUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();
        int count = -1;
        count = db.delete(SQLiteHelper.TODO_TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();
        int count = -1;
        count  = db.update(SQLiteHelper.TODO_TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
