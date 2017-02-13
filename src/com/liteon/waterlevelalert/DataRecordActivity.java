package com.liteon.waterlevelalert;

import java.util.ArrayList;

import com.liteon.waterlevelalert.util.DataAdapter;
import com.liteon.waterlevelalert.util.DataRecord;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class DataRecordActivity extends Activity {

	private ListView mListView;
	private DataAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_record);
		findView();
		setupListView();
	}
	
	private void findView() {
		mListView = (ListView)findViewById(R.id.listview);
		
	}
	
	private void setupListView() {
		ArrayList<DataRecord> list = new ArrayList();
		for (int i = 0; i < 1000; i++) {
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
			list.add(item);
		}
		mAdapter = new DataAdapter(this);
		mAdapter.setDataList(list);
		mListView.setAdapter(mAdapter);
	}
}
