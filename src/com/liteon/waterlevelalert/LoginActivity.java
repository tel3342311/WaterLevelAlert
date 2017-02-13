package com.liteon.waterlevelalert;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private EditText mUserName;
	private EditText mPassword;
	private CheckBox mKeepLogin;
	private Button mLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findView();
		setListener();
	}
	
	private void findView() {
		mUserName = (EditText) findViewById(R.id.user_name);
		mPassword = (EditText) findViewById(R.id.password);
		mKeepLogin = (CheckBox) findViewById(R.id.keep_login);
		mLogin = (Button) findViewById(R.id.login);
	}
	
	private void setListener() {
		mKeepLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
			
		});
		
		mLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				setResult(RESULT_OK);
				finish();
			}
			
		});
	}
}
