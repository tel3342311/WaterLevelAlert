package com.liteon.waterlevelalert;

import java.io.InputStream;

import com.liteon.waterlevelalert.service.AlertDataService;
import com.liteon.waterlevelalert.util.Def;
import com.liteon.waterlevelalert.util.MapImageView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	MapImageView mMap;
	ImageView mBack;
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
			mMonitoring.setVisibility(View.INVISIBLE);
			mThirdary.setVisibility(View.INVISIBLE);
			if (level.equals(Def.WARNING_ALERT)) {
				startAnimation(mWarning, warningAnimation);
				mMap.setImageBitmap(getLocalBitmap(WaterAlertActivity.this, R.drawable.water0alert_gnd_warning));
			} else if (level.equals(Def.SECONDARY_ALERT)) {
				startAnimation(mSecondary, secondaryAnimation);
				mMap.setImageBitmap(getLocalBitmap(WaterAlertActivity.this, R.drawable.water0alert_gnd_secondary));
			} else if (level.equals(Def.THIRDARY_ALERT)) {
				mThirdary.setVisibility(View.VISIBLE);
				mMap.setImageBitmap(getLocalBitmap(WaterAlertActivity.this, R.drawable.water0alert_gnd_third));
			} else {
				
				mMap.setImageBitmap(getLocalBitmap(WaterAlertActivity.this, R.drawable.water0alert_gnd_mapgps));
				mMonitoring.setVisibility(View.VISIBLE);
				startAnimation(mMonitoring, montioringAnimation);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_water_alert);
		findViews();
		setListener();
		setupAnimation();
		mMonitoring.setVisibility(View.VISIBLE);
	}
	
	private void findViews() {
		mWarning = (ImageView) findViewById(R.id.warning);
		mSecondary = (ImageView) findViewById(R.id.secondary_alert);
		mThirdary = (ImageView) findViewById(R.id.thirdary_alert);
		mMap = (MapImageView) findViewById(R.id.map_bg);
		mMonitoring = (ImageView) findViewById(R.id.monitoring_alert);
		mBack = (ImageView) findViewById(R.id.back_btn);
	}
	
	private void setListener() {
		mBack.setOnClickListener(mOnBackClickListener);
	}
	
	private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	}; 
	
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
	
	@Override
	protected void onStart() {
		super.onStart();
		mMap.setImageBitmap(getLocalBitmap(this, R.drawable.water0alert_gnd_mapgps));
	}
}
