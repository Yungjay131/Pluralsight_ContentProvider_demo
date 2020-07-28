package com.slyworks.pluralsight_contentprovider_demo;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.cursoradapter.widget.SimpleCursorAdapter;
//import androidx.loader.app.LoaderManager;
//import androidx.loader.content.CursorLoader;
//import androidx.loader.content.Loader;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.CursorLoader;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.slyworks.pluralsight_contentprovider_demo.data.NationContract;

public class NationListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private  SimpleCursorAdapter simpleCursorAdapter;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nations);


		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabInsertData);

		//method is deprecated, correct way
		//LoaderManager.getInstance(this);

		//old, deprecated way
		//LoaderManager.LoaderCallbacks<Cursor> obj = new NationListActivity();
		/*getSupportLoader*/
		//getLoaderManager().initLoader(10, null, this);
		androidx.loader.app.LoaderManager.getInstance(this);


		String[] from = {NationContract.NationEntry.COLUMN_COUNTRY, NationContract.NationEntry.COLUMN_CONTINENT};
		int[] to = {R.id.txvCountryName, R.id.txvContinentName};

		simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.nation_list_item, null, from, to, 0);

	//making the simpleCourseAdapter the ListView adapter
		final ListView listView =  findViewById(R.id.listView);
		listView.setAdapter(simpleCursorAdapter);

	//setting Listener on listView
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	//each row defines a Cursor Object
				cursor = (Cursor)parent.getItemAtPosition(position);

				int _id = cursor.getInt(cursor.getColumnIndex(NationContract.NationEntry._ID));
				String country = cursor.getString(cursor.getColumnIndex(NationContract.NationEntry.COLUMN_COUNTRY));
				String continent = cursor.getString(cursor.getColumnIndex(NationContract.NationEntry.COLUMN_CONTINENT));

	//starting the NationEditActivity
				Intent intent = new Intent(NationListActivity.this, NationEditActivity.class);

	//passing details to next activity
				intent.putExtra("_id", _id);
				intent.putExtra("country", country);
                intent.putExtra("continent", continent);

                startActivity(intent);

			}
		});
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
	//called whenever a new Loader is created
	//remember to return all the data in a database,
	//selection = null,
	//selectionArgs = null;
	//sortOrder = null;


		String[] projection = {
			NationContract.NationEntry._ID,
			NationContract.NationEntry.COLUMN_COUNTRY,
			NationContract.NationEntry.COLUMN_CONTINENT
		};

		//returns a CursorLoader object that carries a Cursor Object
		//the Cursor object contains all rows queried from database ContentProvider
		return new CursorLoader(this, NationContract.NationEntry.CONTENT_URI, projection, null, null, null);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//called when the Loader has finished loading
		simpleCursorAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//called when a Loader is removed
//destroying the old Cursor and releasing resources
		simpleCursorAdapter.swapCursor(null);
	}
}
