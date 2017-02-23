package com.liteon.waterlevelalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends Activity{

	private VideoView mVideoView;
	private ImageView mPlay;
	private ImageView mVolume;
	private ImageView mFullScreen;
	private TextView mVideoStartTime;
	private TextView mVideoEndTime;
	private ProgressBar mProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		findView();
		setupVideoView();
		setListener();
	}
	
	private void findView() {
		mVideoView = (VideoView) findViewById(R.id.video_view);
		mPlay = (ImageView) findViewById(R.id.play);
		mVolume = (ImageView) findViewById(R.id.volume);
		mFullScreen = (ImageView) findViewById(R.id.fullscreen);
		mVideoStartTime = (TextView) findViewById(R.id.start_progess);
		mVideoEndTime = (TextView) findViewById(R.id.end_progess);
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
 	}
	
	private void setListener() {
		mPlay.setOnClickListener(mOnPlayPasueClickListener);
		mFullScreen.setOnClickListener(mOnFullScreeenClickListener);
		mVolume.setOnClickListener(mOnVolumeClickListener);
	}
	
	private void setupVideoView() {
		Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.rtrs);
		mVideoView.setVideoURI(uri);
		mVideoView.start();
	}
	
	private View.OnClickListener mOnPlayPasueClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
			} else {
				mVideoView.start();
			}
		}
	};
	
	private View.OnClickListener mOnVolumeClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 
					audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), 
					AudioManager.FLAG_SHOW_UI);
		}
	}; 
	
	private View.OnClickListener mOnFullScreeenClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	}; 
}
