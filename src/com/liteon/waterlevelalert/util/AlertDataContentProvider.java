package com.liteon.waterlevelalert.util;

import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AlertDataContentProvider extends ContentProvider {

	private static final String TAG = AlertDataContentProvider.class.getName();
	private static final String AUTHORITY = "com.liteon.waterlevelalert";
	private static final UriMatcher mUriMatcher;
	private static final int URI_ALERT_DATAS = 2;
	private static AlertDataDBHelper mAlertDBHelper;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, AlertEntry.PATH_MULTIPLE, URI_ALERT_DATAS);
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = mAlertDBHelper.getWritableDatabase();
		long row_id = database.insert(AlertEntry.TABLE_NAME, null, values);
		if (row_id < 0) {
			Log.e(TAG, "insert fail");
		}
		Uri curr = ContentUris.withAppendedId(uri, row_id);
		return curr;
	}

	@Override
	public boolean onCreate() {
		mAlertDBHelper = AlertDataDBHelper.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		SQLiteDatabase mDB = null;
		switch(mUriMatcher.match(uri)) {
			case URI_ALERT_DATAS:
				mDB = mAlertDBHelper.getWritableDatabase();
				cursor = mDB.rawQuery(AlertDataDBHelper.SQL_QUERY_ALL_DATA, null);
				cursor.moveToFirst();
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
