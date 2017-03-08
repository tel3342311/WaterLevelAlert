package com.liteon.waterlevelalert;

import android.app.Activity;
import android.content.Intent;
import android.net.rtp.RtpStream;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingActivity extends Activity {

	private static final int REQUEST_CODE = 100;
	private ToggleButton mLoginToggle;
	private Button mSkip;
	private ImageView mBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		findView();
		setListener();
	}
	private void findView() {
		mLoginToggle = (ToggleButton) findViewById(R.id.toggle_login);
		mSkip = (Button) findViewById(R.id.skip);
		mBack = (ImageView) findViewById(R.id.back_btn);
	}
	
	private void setListener() {
		mLoginToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
					startActivityForResult(intent, REQUEST_CODE);
				}
			}
		});
		
		mSkip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		mBack.setOnClickListener(mOnBackClickListener);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        	mLoginToggle.setChecked(true);
        } else {
        	mLoginToggle.setChecked(false);
        }
    }
	
	private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
}
