package com.liteon.waterlevelalert;

import java.util.ArrayList;

import com.liteon.waterlevelalert.util.AlertDataDBHelper;
import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;
import com.liteon.waterlevelalert.util.DataAdapter;
import com.liteon.waterlevelalert.util.DataRecord;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class DataRecordActivity extends Activity {

	private ArrayList<DataRecord> mList;
	private ListView mListView;
	private DataAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_record);
		findView();
		setupListView();
		//new LoadingDataAsyncTask().execute(null);
	}
	
	private void findView() {
		mListView = (ListView)findViewById(R.id.listview);
		
	}
	
	private void setupListView() {
		mAdapter = new DataAdapter(this);
		mAdapter.setDataList(mList);
		mListView.setAdapter(mAdapter);
	}
	
	private void insertData() {
		AlertDataDBHelper alertDBHelper = new AlertDataDBHelper(this);
		for (int i = 0; i < 100; i++) {
			DataRecord item = new DataRecord();
			item.setDate("2017/02/0" + (i%9+1) + " 9:00");
			item.setPoleId("E222123");
			if (i % 3 == 0) {
				item.setStatus("Warning");
			} else if (i % 3 == 1) {
				item.setStatus("Secondary Alert");
			} else {
				item.setStatus("Three Alert");
			}
			
			ContentValues values = new ContentValues();
			values.put(AlertEntry.COLUMN_NAME_DATE, item.getDate());
			values.put(AlertEntry.COLUMN_NAME_POLE_ID, item.getPoleId());
			values.put(AlertEntry.COLUMN_NAME_ALERT_LEVEL, item.getStatus());
			alertDBHelper.getWritableDatabase().insert(AlertEntry.TABLE_NAME, null, values);
		}
	}
	
	private void getData() {
	
		mList = new ArrayList();
		AlertDataDBHelper alertDBHelper = new AlertDataDBHelper(this);
		Cursor cursor = alertDBHelper.getReadableDatabase().rawQuery("SELECT * FROM " + AlertEntry.TABLE_NAME, null);
		int row_num = cursor.getColumnCount();
		cursor.moveToFirst();
		for (int i = 0; i < row_num; i++) {
			DataRecord item = new DataRecord();
			String date = cursor.getString(cursor.getColumnIndex(AlertEntry.COLUMN_NAME_DATE));
			String pole_id = cursor.getString(cursor.getColumnIndex(AlertEntry.COLUMN_NAME_POLE_ID));
			String status = cursor.getString(cursor.getColumnIndex(AlertEntry.COLUMN_NAME_ALERT_LEVEL));
			item.setDate(date);
			item.setPoleId(pole_id);
			item.setStatus(status);
			mList.add(item);
		}
	}
	
//	class LoadingDataAsyncTask extends AsyncTask<Void, Void, Void>{
//	     @Override
//	     protected void doInBackground(Void... param) {
//	          getData();
//	     }
//	     @Override
//	     protected void onPostExecute(Void... param) {
//	          super.onPostExecute();
//	          setupListView();
//	     }
//	     @Override
//	     protected void onProgressUpdate(Void... param) {
//	          super.onProgressUpdate();
//	     }
//	     @Override
//	     protected void onPreExecute() {
//	          super.onPreExecute();
//	     }
//	}
}
