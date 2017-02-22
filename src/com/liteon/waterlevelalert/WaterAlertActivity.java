package com.liteon.waterlevelalert;

import com.liteon.waterlevelalert.service.AlertDataService;
import com.liteon.waterlevelalert.util.Def;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WaterAlertActivity extends Activity {
	
	ImageView mMonitoring;
	ImageView mWarning;
	ImageView mSecondary;
	ImageView mThirdary;
	ImageView mMap;
	Animation warningAnimation; 
	Animation secondaryAnimation;
	AnimationDrawable montioringAnimation;

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String level = intent.getStringExtra(Def.LEVEL);
			mWarning.clearAnimation();
			mSecondary.clearAnimation();
			montioringAnimation.stop();
			mThirdary.setVisibility(View.INVISIBLE);
			if (level.equals(Def.WARNING_ALERT)) {
				startAnimation(mWarning, warningAnimation);
				mMap.setBackgroundResource(R.drawable.water0alert_gnd_warning);
			} else if (level.equals(Def.SECONDARY_ALERT)) {
				startAnimation(mSecondary, secondaryAnimation);
				mMap.setBackgroundResource(R.drawable.water0alert_gnd_secondary);
			} else if (level.equals(Def.THIRDARY_ALERT)) {
				mThirdary.setVisibility(View.VISIBLE);
				mMap.setBackgroundResource(R.drawable.water0alert_gnd_third);
			} else {
				mMap.setBackgroundResource(R.drawable.water0alert_gnd_mapgps);
				startAnimation(mMonitoring, montioringAnimation);
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_water_alert);
		findViews();
		setupAnimation();
		mMonitoring.setVisibility(View.VISIBLE);
	}
	
	private void findViews() {
		mWarning = (ImageView) findViewById(R.id.warning);
		mSecondary = (ImageView) findViewById(R.id.secondary_alert);
		mThirdary = (ImageView) findViewById(R.id.thirdary_alert);
		mMap = (ImageView) findViewById(R.id.map_bg);
		mMonitoring = (ImageView) findViewById(R.id.monitoring_alert);
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
	
	private void startAnimation(ImageView view, Animation animation){		
		view.setAnimation(animation);
	}
	
	private void startAnimation(ImageView view, AnimationDrawable animationDrawable) {
		montioringAnimation.start();
	}
	
	private void setupAnimation(){
		warningAnimation = AnimationUtils.loadAnimation(this, R.anim.warning_alert);
		secondaryAnimation = AnimationUtils.loadAnimation(this, R.anim.secondary_alert);
		montioringAnimation = (AnimationDrawable) mMonitoring.getBackground();		 
	}
}
