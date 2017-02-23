package com.liteon.waterlevelalert;

import java.util.ArrayList;

import com.liteon.waterlevelalert.service.AlertDataService;
import com.liteon.waterlevelalert.util.AlertDataDBHelper;
import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;
import com.liteon.waterlevelalert.util.DataAdapter;
import com.liteon.waterlevelalert.util.DataRecord;
import com.liteon.waterlevelalert.util.Def;

import android.R.bool;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts.Intents.Insert;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.widget.ListView;

public class DataRecordActivity extends Activity {
	private static final String TAG = DataRecordActivity.class.getName();
	private ArrayList<DataRecord> mList = new ArrayList<DataRecord>();
	private ListView mListView;
	private DataAdapter mAdapter;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Uri uri = (Uri)intent.getParcelableExtra(Def.NEW_ALERT_DATA_URI);
			String level = intent.getStringExtra(Def.LEVEL); 
			if (!level.equals(Def.NORMAL)) {
				new loadDataFromDB().execute(uri);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_record);
		findView();
		setupListView();
		new loadDataFromDB().execute(AlertEntry.CONTENT_URI);
	}
	
	private void findView() {
		mListView = (ListView)findViewById(R.id.listview);
		
	}
	
	private void setupListView() {
		mAdapter = new DataAdapter(this);
		mAdapter.setDataList(mList);
		mListView.setAdapter(mAdapter);
	}
	
	private void getData(ArrayList<DataRecord> list, Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null,null, null, null);
		if (cursor.getCount() > 0) {
			do{
				insertData(list,cursor);
			} while (cursor.moveToNext());
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(Def.BROADCAST_ACTION);
		registerReceiver(mBroadcastReceiver , intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBroadcastReceiver);
	}
	private class loadDataFromDB extends AsyncTask<Uri, Integer, Long> {

		private boolean isRetrieveAllData = false;
		private UriMatcher mUriMatcher;
		private ArrayList<DataRecord> tempList = new ArrayList<DataRecord>();
		public loadDataFromDB() {
			mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
			mUriMatcher.addURI(Def.AUTHORITY, AlertEntry.PATH_SINGLE, Def.URI_ALERT_DATA_ID);
			mUriMatcher.addURI(Def.AUTHORITY, AlertEntry.PATH_MULTIPLE, Def.URI_ALERT_DATAS);
		}
		@Override
		protected Long doInBackground(Uri... params) {
			tempList.addAll(mList);
			getData(tempList, params[0]);
			return null;
		}
	
		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			mList.clear();
			mList.addAll(tempList);
			mAdapter.notifyDataSetChanged();
			
			mListView.setSelection(mList.size() - 1);
		}
	}
	
	private void insertData(ArrayList<DataRecord> list, Cursor cursor) {
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
