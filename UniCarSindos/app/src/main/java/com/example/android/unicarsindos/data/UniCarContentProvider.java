package com.example.android.unicarsindos.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by User on 19-Dec-17.
 */

public class UniCarContentProvider extends ContentProvider {


    public static final int USERS=100;
    public static final int USER_WITH_ID=101;

    private static final UriMatcher uriMatcher=buildUriMatcher();

    private UniCarDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(UniCarContract.AUTHORITY, UniCarContract.PATH_USERS, USERS);
        uriMatcher.addURI(UniCarContract.AUTHORITY, UniCarContract.PATH_USERS + "/#", USER_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context=getContext();
        mDbHelper = new UniCarDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor retCursor;

        final SQLiteDatabase db= mDbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);

        switch (match){

            case USERS:
                retCursor=db.query(UniCarContract.UniCarEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Uri retUri;

        final SQLiteDatabase db= mDbHelper.getWritableDatabase();

        int match= uriMatcher.match(uri);

        switch (match){
            case USERS:
                long id = db.insert(UniCarContract.UniCarEntry.TABLE_NAME,null,contentValues);
                //Log.e("ID from insert--->", Long.toString(id));
                if(id>=0){
                    retUri = ContentUris.withAppendedId(UniCarContract.UniCarEntry.CONTENT_URI,id);
                }else{
                    throw new android.database.SQLException("Failed to insert row with id :"+ Long.toString(id)+", into "+uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {

        final SQLiteDatabase db= mDbHelper.getWritableDatabase();
        int deletedItemsCount=0;
        int match= uriMatcher.match(uri);

        switch (match) {
            case USERS:
                deletedItemsCount = db.delete(UniCarContract.UniCarEntry.TABLE_NAME, whereClause, whereArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return deletedItemsCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String whereClause,
                      @Nullable String[] whereArgs) {
        int updatedItemsCount=0;

        final SQLiteDatabase db= mDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match){
            case USERS:
                updatedItemsCount= db.update(UniCarContract.UniCarEntry.TABLE_NAME,
                        contentValues,whereClause,whereArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return updatedItemsCount;
    }
}
