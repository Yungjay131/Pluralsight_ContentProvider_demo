package com.slyworks.pluralsight_contentprovider_demo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NationProvider extends ContentProvider {
 private static final String TAG = NationProvider.class.getSimpleName();

    //declaring instance of database helper class
    private NationDbHelper databaseHelper;

 //constants for the Uri operation
    private static final int COUNTRIES = 1;
    private static final int COUNTRIES_COUNTRY_NAME = 2;
    private static final int COUNTRIES_ID = 3;

 //for ContentProvider Uri
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static block for Uri, code should be +ve integer
    static{
        //creating different variations to match different patterns
        //and matches the passed Uri from another activity

        //1st - for whole table
        //2nd - for row id
        //3rd - for specific row in db Table
        //they should be arranged in the right priority i.e the sequence/order of the patterns is important
        sUriMatcher.addURI(NationContract.CONTENT_AUTHORITY, NationContract.PATH_COUNTRIES, COUNTRIES);
        sUriMatcher.addURI(NationContract.CONTENT_AUTHORITY, NationContract.PATH_COUNTRIES + "/#", COUNTRIES_ID);
        sUriMatcher.addURI(NationContract.CONTENT_AUTHORITY, NationContract.PATH_COUNTRIES + "/*", COUNTRIES_COUNTRY_NAME);

        ;
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new NationDbHelper(getContext());

        //returning true to indicate the initial setup is done successfully
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sq_db = databaseHelper.getReadableDatabase();
        Cursor cursor;

        switch(sUriMatcher.match(uri)){
            case COUNTRIES:
                 cursor = sq_db.query(NationContract.NationEntry.TABLE_NAME,		// The table name
                        projection,                 // The columns to return
                        null,                  // Selection: WHERE clause OR the condition
                        null,              // Selection Arguments for the WHERE clause
                        null,                       // don't group the rows
                        null,                       // don't filter by row groups
                        sortOrder);					// The sort order


                break;
            case COUNTRIES_ID:
                //specifying some arguments
                selection = NationContract.NationEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};

                cursor = sq_db.query(NationContract.NationEntry.TABLE_NAME,		// The table name
                        projection,                 // The columns to return
                        selection,                  // Selection: WHERE clause OR the condition
                        selectionArgs,              // Selection Arguments for the WHERE clause
                        null,                       // don't group the rows
                        null,                       // don't filter by row groups
                        sortOrder);					// The sort order
                break;
            default:
                 throw  new IllegalArgumentException(TAG + "Querying unknown Uri: "+uri);
        }

        //Cursor object being returned is received in main Activity
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase sq_db = databaseHelper.getWritableDatabase();

        //matching the passed parameter Uri with matchers
        switch(sUriMatcher.match(uri)){
            case COUNTRIES:
                return insertRecord(uri,values,  NationContract.NationEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException(TAG + "Inserted unknown Uri:"+ uri);
        }

    }

    private Uri insertRecord(Uri uri,ContentValues values, String tableName) {
    SQLiteDatabase sq_db = databaseHelper.getWritableDatabase();
    long rowID = sq_db.insert(NationContract.NationEntry.TABLE_NAME, null, values);

    //if something went wrong during insert operation
        if(rowID == -1){
            Log.e(TAG,"error occurred during insert operation"+ uri);
            return null;
        }
    return ContentUris.withAppendedId(uri, rowID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
       //assuming the contentProvider is content://com.slyworks.pluralsight_ContentProvider_demo.data.NationProvider/countries

        switch(sUriMatcher.match(uri)){
           //to delete whole table
           case COUNTRIES:
               return deletedRecord(null, null , NationContract.NationEntry.TABLE_NAME);
           //for the case of rowID being passed
           case COUNTRIES_ID:

               //to avoid deleting the whole row based on selection parameters passed in
               selection = NationContract.NationEntry._ID+" = ?";
               selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

               return deletedRecord(selection, selectionArgs, NationContract.NationEntry.TABLE_NAME);

           case COUNTRIES_COUNTRY_NAME:
               selection = NationContract.NationEntry.COLUMN_COUNTRY;

               //to get the last part of the Uri entered(country name)
               selectionArgs = new String[]{uri.getLastPathSegment()};
               return deletedRecord(selection,selectionArgs, NationContract.NationEntry.TABLE_NAME);
           default:
               throw new IllegalArgumentException(TAG+ " Inserted unknown uri:"+ uri);
       }
    }

    private int deletedRecord(String selection, String[] selectionArgs, String tableName) {
    SQLiteDatabase sq_db = databaseHelper.getWritableDatabase();
    int rowsDeleted = sq_db.delete( tableName,selection, selectionArgs);
    return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)){
            case COUNTRIES:
                return updateRecord(values, selection, selectionArgs, NationContract.NationEntry.TABLE_NAME);
            default:
                throw  new IllegalArgumentException(TAG + " Inserted unknown Uri: "+ uri);
        }
    }

    private int updateRecord(ContentValues values, String selection, String[] selectionArgs, String tableName) {
        SQLiteDatabase sq_db = databaseHelper.getWritableDatabase();
        int rowsUpdated = sq_db.update(tableName, values, selection,selectionArgs);
        return rowsUpdated;
    }
}
