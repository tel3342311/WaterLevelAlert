package com.liteon.waterlevelalert;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity{

	private VideoView mVideoView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		findView();
		setupVideoView();
	}
	
	private void findView() {
		mVideoView = (VideoView) findViewById(R.id.video_view);
		
	}
	
	private void setupVideoView() {
		Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.samplevideo);
		mVideoView.setVideoURI(uri);
		mVideoView.start();
	}
}
