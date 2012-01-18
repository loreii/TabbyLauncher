package org.tabbylauncher.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TabbyProvider extends ContentProvider{ 

	private static final int DATABASE_VERSION = 2;

	private static final String TAG_TABLE_NAME = "ApplicationsTags";
	private static final String DATABASE_NAME  = "tabby.db";

	


	private static final String DICTIONARY_TABLE_CREATE =
			"CREATE TABLE " + TAG_TABLE_NAME + " (" +
					Tabby.Applications.PACKAGE  + " TEXT PRIMARY KEY, " +
					Tabby.Applications.TAG 		+ " TEXT, " +
					Tabby.Applications.COLOR 	+ " INTEGER);";


	static class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DICTIONARY_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}
	}

	private DBHelper mOpenHelper;

	private static final UriMatcher sUriMatcher;
	private static final int FAVORITE    = 0;
	private static final int FAVORITE_ID = 1;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Tabby.AUTHORITY, "favorites",   FAVORITE);
		sUriMatcher.addURI(Tabby.AUTHORITY, "favorites/#", FAVORITE_ID);
	}

	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		 switch (sUriMatcher.match(uri)) {
	        case FAVORITE:
	            return Tabby.Applications.CONTENT_TYPE;

	        case FAVORITE_ID:
	            return Tabby.Applications.CONTENT_ITEM_TYPE;

	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 // Validate the requested uri
        if (sUriMatcher.match(uri) != FAVORITE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(TAG_TABLE_NAME, Tabby.Applications.THAT, values);
        if (rowId > 0) {
            Uri applicationUri = ContentUris.withAppendedId(Tabby.Applications.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(applicationUri, null);
            return applicationUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		switch (sUriMatcher.match(uri)) {
		case FAVORITE:
			//qb.setProjectionMap(sProjectionMap);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TAG_TABLE_NAME);

		// Opens the database object in "read" mode, since no writes need to be done.
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		/*
		 * Performs the query. If no problems occur trying to read the database, then a Cursor
		 * object is returned; otherwise, the cursor variable contains null. If no records were
		 * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
		 */
		Cursor c = qb.query(
				db,            // The database to query
				projection,    // The columns to return from the query
				selection,     // The columns for the where clause
				selectionArgs, // The values for the where clause
				null,          // don't group the rows
				null,          // don't filter by row groups
				sortOrder      // The sort order
				);

		// Tells the Cursor what URI to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;


	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		return 0;
		//TODO
		/*
		 SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	        int count = 0;
	        switch (sUriMatcher.match(uri)) {
	        case FAVORITE:
//	            count = db.update(TAG_TABLE_NAME, values, where, whereArgs);
	            break;

	        case FAVORITE_ID:
//	            String fId = uri.getPathSegments().get(1);
//	            count = db.update(TAG_TABLE_NAME, values, Tabby.Applications._ID + "=" + fId
//	                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
	            break;

	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;*/
	}

}