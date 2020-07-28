package com.slyworks.pluralsight_contentprovider_demo.data;


import android.net.Uri;
import android.provider.BaseColumns;

/* A contract class contains constants that define names for URIs, tables, and columns.  */
public final class NationContract {
//defining the Authority String for ContentProvider, and
//then copy "applicationID from build.gradle
	public static  final String CONTENT_AUTHORITY = "com.slyworks.pluralsight_contentprovider_demo.data.NationProvider";

	//BASE_CONTENT_URI: content://com.slyworks.pluralsight_contentprivider_demo.data.NationProvider
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);


	//path to get client App to our dbtable
	public static final String PATH_COUNTRIES = "countries";

	//inner class has to implement BaseColumns to define the individual columns in the Database
	//1 inner class per Database table
	public static final class NationEntry implements BaseColumns {

		//declaring the Content Uri,(remember to declare in manifest.xml)
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COUNTRIES);

		// Table Name
		public static final String TABLE_NAME = "countries";

		// Columns
		public static final String _ID = BaseColumns._ID;
		public static final String COLUMN_COUNTRY = "country";
		public static final String COLUMN_CONTINENT = "continent";
	}
}
