package com.liteon.waterlevelalert;

import com.liteon.waterlevelalert.service.AlertDataService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private ImageView mMainView;
	private ImageView mWaterAlertView;
	private ImageView mVideoView;
	private ImageView mDataRecordView;
	private ImageView mSettingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		setListener();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		startAlertService();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void findViews() {
		mWaterAlertView = (ImageView) findViewById(R.id.alert_btn);
		mVideoView = (ImageView) findViewById(R.id.video_btn);
		mDataRecordView = (ImageView) findViewById(R.id.data_btn);
		mSettingView = (ImageView) findViewById(R.id.setting_btn);
	}
	private void setListener() {
		mWaterAlertView.setOnClickListener(mOnClickListener);
		mVideoView.setOnClickListener(mOnClickListener);
		mDataRecordView.setOnClickListener(mOnClickListener);
		mSettingView.setOnClickListener(mOnClickListener);
	}
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = null;
			if (v.getId() == R.id.alert_btn) {
				intent = new Intent(MainActivity.this, WaterAlertActivity.class);
			} else if (v.getId() == R.id.video_btn) {
				intent = new Intent(MainActivity.this, VideoActivity.class);
			} else if (v.getId() == R.id.data_btn) {
				intent = new Intent(MainActivity.this, DataRecordActivity.class);
			} else if (v.getId() == R.id.setting_btn) {
				intent = new Intent(MainActivity.this, SettingActivity.class);
			} else {
				
			}
			startActivity(intent);
		}
	};
	
	protected void onPause() {
		super.onPause();
		stopAlertService();
	};
	@Override
	protected void onStop() {
		super.onStop();
		stopAlertService();
	}
	private void startAlertService(){
		Intent intent = new Intent(MainActivity.this,
                AlertDataService.class);
        startService(intent);
	}
	
	private void stopAlertService(){
		Intent intent = new Intent(MainActivity.this,
                AlertDataService.class);
        stopService(intent);
	}
}
