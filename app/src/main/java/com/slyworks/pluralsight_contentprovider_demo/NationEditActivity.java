package com.slyworks.pluralsight_contentprovider_demo;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.slyworks.pluralsight_contentprovider_demo.data.NationContract;

public class NationEditActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String TAG = NationEditActivity.class.getSimpleName();

	private EditText etCountry, etContinent;
	private Button btnUpdate, btnDelete, btnInsert;

	int _id;
	String country, continent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nations_edit);

		etCountry 	= (EditText) findViewById(R.id.etCountry);
		etContinent = (EditText) findViewById(R.id.etContinent);

		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnInsert = (Button) findViewById(R.id.btnInsert);

		Intent intent = getIntent();
		_id = intent.getIntExtra("_id", 0);
		country = intent.getStringExtra("country");
		continent = intent.getStringExtra("continent");

		if (_id != 0) {		// We want to delete or update data
			etCountry.setText(country);
			etContinent.setText(continent);
			btnInsert.setVisibility(View.GONE);
		} else {			// We want to insert data
			btnUpdate.setVisibility(View.GONE);
			btnDelete.setVisibility(View.GONE);
		}

		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnInsert.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.btnUpdate:
					update();
				break;

			case R.id.btnDelete:
					delete();
				break;

			case R.id.btnInsert:
					insert();
				break;
		}
	}

	private void update() {
		//to update row that satisfies condition
		String selection = NationContract.NationEntry._ID+" =? ";
		String[] selectionArgs = {String.valueOf(_id)};


		//getting the edited/new values from the EditTexts
		ContentValues contentValues = new ContentValues();
		contentValues.put(NationContract.NationEntry.COLUMN_COUNTRY, etCountry.getText().toString());
		contentValues.put(NationContract.NationEntry.COLUMN_CONTINENT, etContinent.getText().toString());

		//this would appear as: content://com.slyworks.pluralsight_contentProvider_demo.data.NationProvider/countries/_id
		Uri uri = NationContract.NationEntry.CONTENT_URI;

		int rowsUpdated = getContentResolver().delete(uri, selection, selectionArgs);
		Log.e(TAG, "Number of rows deleted: "+ rowsUpdated);
	}

	private void delete() {

		String selection = NationContract.NationEntry._ID+" =? ";
		String[] selectionArgs = {String.valueOf(_id)};

		//this would appear as: content://com.slyworks.pluralsight_contentProvider_demo.data.NationProvider/countries/_id
		Uri uri = ContentUris.withAppendedId(NationContract.NationEntry.CONTENT_URI, _id);

		int rowsDeleted = getContentResolver().delete(uri, selection, selectionArgs);
        Log.e(TAG, "Number of rows deleted: "+ rowsDeleted);
	}

	private void insert() {

	}
}
