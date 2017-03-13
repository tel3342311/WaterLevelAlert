package com.liteon.waterlevelalert;

import java.io.InputStream;

import com.liteon.waterlevelalert.service.AlertDataService;
import com.liteon.waterlevelalert.util.MapImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private ImageView mWaterAlertView;
	private ImageView mVideoView;
	private ImageView mDataRecordView;
	private ImageView mSettingView;
	private MapImageView mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		setListener();
		startAlertService();
		new LoadBitmapTask().execute();
	}
	
	@Override
	protected void onStart() {
		super.onStart();		
	}
	
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
		mMap = (MapImageView) findViewById(R.id.map_bg);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	
	public Bitmap getLocalBitmap(Context con, int resourceId){
	    InputStream inputStream = con.getResources().openRawResource(resourceId);
	    return BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(1));
	}
	
	private BitmapFactory.Options getBitmapOptions(int scale){
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inPurgeable = true;
	    options.inInputShareable = true;
	    options.inSampleSize = scale;
	    return options;
	}
	
	private class LoadBitmapTask extends AsyncTask<Void, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			return getLocalBitmap(MainActivity.this, R.drawable.water0alert_gnd_mapgps);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mMap.setImageBitmap(result);
		}
	}
}
