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

	private ArrayList<DataRecord> mList = new ArrayList<DataRecord>();
	private ListView mListView;
	private DataAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_record);
		findView();
		setupListView();
		new loadDataFromDB().execute(mList);
	}
	
	private void findView() {
		mListView = (ListView)findViewById(R.id.listview);
		
	}
	
	private void setupListView() {
		mAdapter = new DataAdapter(this);
		mAdapter.setDataList(mList);
		mListView.setAdapter(mAdapter);
	}
	
	private void getData(ArrayList<DataRecord> list) {
		if (list == null) {
			list = new ArrayList();
		}
		list.clear();
		AlertDataDBHelper alertDBHelper = AlertDataDBHelper.getInstance(getApplicationContext());
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
			list.add(item);
		}
	}
	
	private class loadDataFromDB extends AsyncTask<ArrayList<DataRecord>, Integer, Long> {

		@Override
		protected Long doInBackground(ArrayList<DataRecord>... params) {
			getData(params[0]);
			return null;
		}
	
		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			mAdapter.notifyDataSetChanged();
		}
	}
}
