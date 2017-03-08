package com.liteon.waterlevelalert;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends Activity{

	private VideoView mVideoView;
	private ImageView mPlay;
	private ImageView mVolume;
	private ImageView mFullScreen;
	private ImageView mBack;
	private ImageView mVideoStatus;
	private TextView mVideoStartTime;
	private TextView mVideoEndTime;
	private SeekBar mProgress;
	private int mDuration;
	private Timer mUpdateUITimer;
	private boolean isComplete;
	private Animation videoStatus;
	private View mVideoControl;
	private View mRootLayout;
	private boolean mIsHideControl;
	
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
	    mUpdateUITimer.schedule(task, 0, 50);
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
		setupAnimation();
	}
	
	private void findView() {
		mVideoView = (VideoView) findViewById(R.id.video_view);
		mPlay = (ImageView) findViewById(R.id.play);
		mVolume = (ImageView) findViewById(R.id.volume);
		mFullScreen = (ImageView) findViewById(R.id.fullscreen);
		mVideoStartTime = (TextView) findViewById(R.id.start_progess);
		mVideoEndTime = (TextView) findViewById(R.id.end_progess);
		mProgress = (SeekBar) findViewById(R.id.progress_bar);
		mBack = (ImageView) findViewById(R.id.back_btn);
		mVideoStatus = (ImageView) findViewById(R.id.video_status);
		mVideoControl = findViewById(R.id.video_control);
		mRootLayout = findViewById(R.id.video_bg);
 	}
	
	private void setListener() {
		mPlay.setOnClickListener(mOnPlayPasueClickListener);
		mFullScreen.setOnClickListener(mOnFullScreeenClickListener);
		mVolume.setOnClickListener(mOnVolumeClickListener);
		mBack.setOnClickListener(mOnBackClickListener);
		mProgress.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		mVideoView.setOnClickListener(mOnVideoViewClickListener);
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
				mVideoStatus.setBackgroundResource(R.drawable.pause_btn);
				mVideoStatus.startAnimation(videoStatus);
			} else {
				if (isComplete) {
					setupVideoView();
					isComplete = false;
				} else {
					mVideoView.start();
				}
				mVideoStatus.setBackgroundResource(R.drawable.video_btn_play2);
				mVideoStatus.startAnimation(videoStatus);
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
			hideControl();
		}
	}; 
	
	private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
	
	private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				stopUITimer();
				int position = mDuration * progress / 100; 
				mVideoView.seekTo(position);
				updateUI();
				startUITimer();
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
	};
	
	private View.OnClickListener mOnVideoViewClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mIsHideControl) {
				showControl();
			}
		}
	};
	private void setDuration(){
		mDuration = mVideoView.getDuration();
		mVideoEndTime.setText(getProgressString(mDuration));
	}
	
	private void updateUI() {
		int current = mVideoView.getCurrentPosition();
		int progress = current * 100 / mDuration;
		mProgress.setProgress(progress);
		mVideoStartTime.setText(getProgressString(current));
	}
	
	private String getProgressString(int duration) {
		duration /= 1000;
		int minutes = (duration / 60);
        int seconds = duration - (minutes * 60) ;
        return String.format("%02d:%02d", minutes, seconds);
	}
	
	private void setupAnimation(){
		videoStatus = AnimationUtils.loadAnimation(this, R.anim.video_status);
	}
	
	private void hideControl() {
		mVideoControl.setVisibility(View.INVISIBLE);
		mRootLayout.setBackground(null);
		mRootLayout.setBackgroundColor(0xFF000000);
		mBack.setVisibility(View.INVISIBLE);
		mIsHideControl = true;
	}
	
	private void showControl() {
		mVideoControl.setVisibility(View.VISIBLE);
		mRootLayout.setBackground(getResources().getDrawable(R.drawable.video_gnd));
		mBack.setVisibility(View.VISIBLE);
		mIsHideControl = false;
	}
}
