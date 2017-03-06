package com.liteon.waterlevelalert;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends Activity{

	private VideoView mVideoView;
	private ImageView mPlay;
	private ImageView mVolume;
	private ImageView mFullScreen;
	private ImageView mBack;
	private TextView mVideoStartTime;
	private TextView mVideoEndTime;
	private ProgressBar mProgress;
	private int mDuration;
	private Timer mUpdateUITimer;
	private boolean isComplete;
	private LinearLayout mRootLayout;
	
	private void startUITimer() {
		mUpdateUITimer = new Timer();
	    TimerTask task = new TimerTask() {
	        @Override
	        public void run() {
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                    updateUI();
	                }
	            });
	        }
	    };
	    mUpdateUITimer.schedule(task, 0, 100);
	}
	
	private void stopUITimer() {
		mUpdateUITimer.cancel();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		findView();
		setupVideoView();
		setListener();
	}
	
	private void findView() {
		mRootLayout = (LinearLayout) findViewById(R.id.video_bg);
		mVideoView = (VideoView) findViewById(R.id.video_view);
		mPlay = (ImageView) findViewById(R.id.play);
		mVolume = (ImageView) findViewById(R.id.volume);
		mFullScreen = (ImageView) findViewById(R.id.fullscreen);
		mVideoStartTime = (TextView) findViewById(R.id.start_progess);
		mVideoEndTime = (TextView) findViewById(R.id.end_progess);
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
		mBack = (ImageView) findViewById(R.id.back_btn);
 	}
	
	private void setListener() {
		mPlay.setOnClickListener(mOnPlayPasueClickListener);
		mFullScreen.setOnClickListener(mOnFullScreeenClickListener);
		mVolume.setOnClickListener(mOnVolumeClickListener);
		mBack.setOnClickListener(mOnBackClickListener);
	}
	
	private void setupVideoView() {
		Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.rtrs);
		mVideoView.setVideoURI(uri);
		mVideoView.start();
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				setDuration();	
				startUITimer();
			}
		});
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				isComplete = true;
				mProgress.setProgress(100);
				stopUITimer();
			}
			
		});
	}
	
	private View.OnClickListener mOnPlayPasueClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
			} else {
				if (isComplete) {
					setupVideoView();
					isComplete = false;
				} else {
					mVideoView.start();
				}
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
			makeVideoFullScreen();
		}
	}; 
	
	private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
	
	private void setDuration(){
		mDuration = mVideoView.getDuration();
	}
	
	private void updateUI() {
		int current = mVideoView.getCurrentPosition();
		int progress = current * 100 / mDuration;
		mProgress.setProgress(progress);
	}
	
	private LinearLayout.LayoutParams defaultVideoViewParams;
	private int defaultScreenOrientationMode;

	// play video in fullscreen mode
	private void makeVideoFullScreen() {

	    defaultScreenOrientationMode = getResources().getConfiguration().orientation;
	    defaultVideoViewParams = (LayoutParams) mVideoView.getLayoutParams();

	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
 
    	mVideoView.postDelayed(new Runnable() {

	        @Override
	        public void run() {
	        	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(
	        			LinearLayout.LayoutParams.MATCH_PARENT,
	        			LinearLayout.LayoutParams.MATCH_PARENT);
	            mVideoView.setLayoutParams(params);
	            mVideoView.layout(10, 10, 10, 10);
	        }
	    }, 700);
	}


	// close fullscreen mode
	private void exitVideoFullScreen() {
	    setRequestedOrientation(defaultScreenOrientationMode);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

	    mVideoView.postDelayed(new Runnable() {

	        @Override
	        public void run() {
	        	mVideoView.setLayoutParams(defaultVideoViewParams);
	        	mVideoView.layout(10, 10, 10, 10);
	        }
	    }, 700);
	}
}
