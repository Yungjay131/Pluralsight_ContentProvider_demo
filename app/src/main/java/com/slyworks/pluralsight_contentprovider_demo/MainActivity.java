package com.slyworks.pluralsight_contentprovider_demo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.slyworks.pluralsight_contentprovider_demo.data.NationContract.NationEntry;
import com.slyworks.pluralsight_contentprovider_demo.data.NationDbHelper;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private EditText etCountry, etContinent, etWhereToUpdate, etNewContinent, etWhereToDelete, etQueryRowById;
	private Button btnInsert, btnUpdate, btnDelete, btnQueryRowById, btnDisplayAll;

	private static final String TAG = MainActivity.class.getSimpleName();

	private SQLiteDatabase database;

	//now using ContentProvider
	//private NationDbHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etCountry 		=  findViewById(R.id.etCountry);
		etContinent 	= (EditText) findViewById(R.id.etContinent);
		etWhereToUpdate = (EditText) findViewById(R.id.etWhereToUpdate);
		etNewContinent 	= (EditText) findViewById(R.id.etUpdateContinent);
		etQueryRowById 	= (EditText) findViewById(R.id.etQueryByRowId);
		etWhereToDelete = (EditText) findViewById(R.id.etWhereToDelete);

		btnInsert 		= (Button) findViewById(R.id.btnInsert);
		btnUpdate 		= (Button) findViewById(R.id.btnUpdate);
		btnDelete 		= (Button) findViewById(R.id.btnDelete);
		btnQueryRowById = (Button) findViewById(R.id.btnQueryByID);
		btnDisplayAll 	= (Button) findViewById(R.id.btnDisplayAll);

		btnInsert.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnQueryRowById.setOnClickListener(this);
		btnDisplayAll.setOnClickListener(this);


		//to connect to SQLiteDatabase
		//now using ContentProvider, hence these are not needed
		//databaseHelper = new NationDbHelper(this);
		//database = databaseHelper.getWritableDatabase();		// READ/WRITE
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.btnInsert:
				insert();
				break;

			case R.id.btnUpdate:
				update();
				break;

			case R.id.btnDelete:
				delete();
				break;

			case R.id.btnQueryByID:
				queryRowById();
				break;

			case R.id.btnDisplayAll:
				queryAndDisplayAll();
				break;
		}
	}

	private void insert() {

		String countryName 	= etCountry.getText().toString();
		String continentName= etContinent.getText().toString();

		ContentValues contentValues = new ContentValues();
		contentValues.put(NationEntry.COLUMN_COUNTRY, countryName);
		contentValues.put(NationEntry.COLUMN_CONTINENT, continentName);

		//no longer necessary because of contentProvider,instead using contentProvider
		//subclass - NationProvider's insert method
		//long rowId = database.insert(NationEntry.TABLE_NAME, null, contentValues);
		//Log.i(TAG, "Items inserted in table with row id: " + rowId);

	//adding to db, by sending to ContentProvider class
		Uri uri = NationEntry.CONTENT_URI;
		Uri uriRowInserted = getContentResolver().insert(uri,contentValues);
	    Log.e(TAG, "Items inserted at: "+ uriRowInserted);
	}

	private void update() {

		String whereCountry = etWhereToUpdate.getText().toString();
		String newContinent = etNewContinent.getText().toString();

		String selection = NationEntry.COLUMN_COUNTRY + " = ?";
		String[] selectionArgs = { whereCountry };			// WHERE country = ? = Japan

		ContentValues contentValues = new ContentValues();
		contentValues.put(NationEntry.COLUMN_CONTINENT, newContinent);

		//now handled in ContentProvider subclass
		//int rowsUpdated = database.update(NationEntry.TABLE_NAME, contentValues, selection, selectionArgs);
		//Log.i(TAG, "Number of rows updated: " + rowsUpdated);

		Uri uri = NationEntry.CONTENT_URI;
		Log.e(TAG, ""+uri);
	    int rowsUpdated =  getContentResolver().update(uri, contentValues,selection, selectionArgs);
	    Log.e(TAG, "Number of rows updated: "+rowsUpdated);
	}

	private void delete() {

		String countryName = etWhereToDelete.getText().toString();

		String selection = NationEntry.COLUMN_COUNTRY + " = ? ";
		String[] selectionArgs = { countryName };		// WHERE country = "Japan"

		//int rowsDeleted = database.delete(NationEntry.TABLE_NAME, selection, selectionArgs);
		//Log.i(TAG, "Number of rows deleted: " + rowsDeleted);

	    Uri uri = Uri.withAppendedPath(NationEntry.CONTENT_URI, countryName/*could be rowID*/);
	    Log.e(TAG, ""+uri);
	    int rowsDeleted = getContentResolver().delete(uri, selection, selectionArgs);
	}

	private void queryRowById() {

		String rowId = etQueryRowById.getText().toString();

		String[] projection = {
				NationEntry._ID,
				NationEntry.COLUMN_COUNTRY,
				NationEntry.COLUMN_CONTINENT
		};


		// Filter results. Make these null if you want to query all rows
		String selection = NationEntry._ID + " = ? ";	// _id = ?
		String[] selectionArgs = { rowId };				// Replace '?' by rowId in runtime		// _id = 5

		String sortOrder = null;	// Ascending or Descending ...

		/*
		no longer necessary being implemented in NationProvider

		Cursor cursor = database.query(NationEntry.TABLE_NAME,		// The table name
				projection,                 // The columns to return
				selection,                  // Selection: WHERE clause OR the condition
				selectionArgs,              // Selection Arguments for the WHERE clause
				null,                       // don't group the rows
				null,                       // don't filter by row groups
				sortOrder);					// The sort order
        */
//for ContentProvider
		Uri uri = Uri.withAppendedPath(NationEntry.CONTENT_URI, rowId);

		Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);


		if (cursor != null && cursor.moveToNext()) {

			String str = "";

			String[] columns = cursor.getColumnNames();
			for (String column : columns) {
				str += "\t" + cursor.getString(cursor.getColumnIndex(column));
			}
			str += "\n";

			cursor.close();
			cursor.close();
			Log.i(TAG, str);
		}
	}

	private void queryAndDisplayAll() {



		String[] projection = {
				NationEntry._ID,
				NationEntry.COLUMN_COUNTRY,
				NationEntry.COLUMN_CONTINENT
		};

		// Filter results. Make these null if you want to query all rows
		String selection = null;
		String[] selectionArgs = null;

		String sortOrder = null;	// Ascending or Descending ...

		/*no longer used because of ContentProvider

		Cursor cursor = database.query(NationEntry.TABLE_NAME,		// The table name
				projection,                 // The columns to return
				selection,                  // Selection: WHERE clause OR the condition
				selectionArgs,              // Selection Arguments for the WHERE clause
				null,                       // don't group the rows
				null,                       // don't filter by row groups
				sortOrder);					// The sort order
*/

		//for ContentProvider
		Uri uri = NationEntry.CONTENT_URI;
		Log.e(TAG,""+uri);
		Cursor cursor = getContentResolver().query(uri, projection, selection,selectionArgs, sortOrder);

		if (cursor != null) {

			String str = "";
			while (cursor.moveToNext()) {	// Cursor iterates through all rows

				String[] columns = cursor.getColumnNames();
				for (String column : columns) {
					str += "\t" + cursor.getString(cursor.getColumnIndex(column));
				}
				str += "\n";
			}

			cursor.close();
			Log.e(TAG, str);

			//adding intent to open NationListActivity
			Intent intent = new Intent(this, NationListActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		database.close();	// Close Database Connection
		super.onDestroy();
	}
}
