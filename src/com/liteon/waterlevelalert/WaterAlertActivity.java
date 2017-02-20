package com.liteon.waterlevelalert;

import com.liteon.waterlevelalert.service.AlertDataService;
import com.liteon.waterlevelalert.util.Def;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WaterAlertActivity extends Activity {
	
	TextView mTextView;
	ImageView mWarning;
	ImageView mSecondary;
	ImageView mThirdary;
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String level = intent.getStringExtra(Def.LEVEL);
			mTextView.setText("");
			mWarning.setVisibility(View.INVISIBLE);
			mSecondary.setVisibility(View.INVISIBLE);
			mThirdary.setVisibility(View.INVISIBLE);
			if (level.equals(Def.WARNING_ALERT)) {
				mTextView.setText(R.string.warning);
				mWarning.setVisibility(View.VISIBLE);
			} else if (level.equals(Def.SECONDARY_ALERT)) {
				mTextView.setText(R.string.secondary_alert);
				mSecondary.setVisibility(View.VISIBLE);
			} else if (level.equals(Def.THIRDARY_ALERT)) {
				mTextView.setText(R.string.thirdary_alert);
				mThirdary.setVisibility(View.VISIBLE);
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_water_alert);
		findViews();
	}
	
	private void findViews() {
		mTextView = (TextView) findViewById(R.id.alert_status);
		mWarning = (ImageView) findViewById(R.id.warning);
		mSecondary = (ImageView) findViewById(R.id.secondary_alert);
		mThirdary = (ImageView) findViewById(R.id.thirdary_alert);
	}
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(Def.BROADCAST_ACTION);
		registerReceiver(mBroadcastReceiver, intentFilter);
		Intent intent = new Intent(this, AlertDataService.class);
		intent.setAction(Def.BROADCAST_ACTION_GET_LEVEL);
		startService(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBroadcastReceiver);
	}
}
