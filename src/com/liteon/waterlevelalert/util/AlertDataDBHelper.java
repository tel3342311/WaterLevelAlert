package com.liteon.waterlevelalert.util;

import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlertDataDBHelper extends SQLiteOpenHelper {

	private static AlertDataDBHelper mInstance = null;
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AlertData.db";
    public static final String SQL_QUERY_ALL_DATA = "SELECT * FROM " + AlertEntry.TABLE_NAME;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
    	    "CREATE TABLE " + AlertEntry.TABLE_NAME + " (" +
    	    AlertEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    	    AlertEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
    	    AlertEntry.COLUMN_NAME_POLE_ID + TEXT_TYPE + COMMA_SEP +
    	    AlertEntry.COLUMN_NAME_ALERT_LEVEL + TEXT_TYPE +
    	    " )";
    
    
    private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + AlertEntry.TABLE_NAME;
    
    public static AlertDataDBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new AlertDataDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    
    private AlertDataDBHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
}
