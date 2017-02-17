package com.liteon.waterlevelalert;

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
			
			String level = intent.getStringExtra("level");
			mTextView.setText("");
			mWarning.setVisibility(View.INVISIBLE);
			mSecondary.setVisibility(View.INVISIBLE);
			mThirdary.setVisibility(View.INVISIBLE);
			if (level.equals("Warning")) {
				mTextView.setText("WARNING!");
				mWarning.setVisibility(View.VISIBLE);
			} else if (level.equals("Secondary Alert")) {
				mTextView.setText("Secondary Alert");
				mSecondary.setVisibility(View.VISIBLE);
			} else if (level.equals("Thirdary Alert")) {
				mTextView.setText("Thirdary Alert");
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
		String BROADCAST_ACTION = "com.liteon.waterlevelalert.alertevent";
		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(mBroadcastReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBroadcastReceiver);
	}
}
