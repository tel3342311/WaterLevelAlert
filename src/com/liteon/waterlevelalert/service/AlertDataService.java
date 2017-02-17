package com.liteon.waterlevelalert.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;
import com.liteon.waterlevelalert.util.SocketConnection;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class AlertDataService extends Service {

	private static final String TAG = AlertDataService.class.getName();
	private static DataThread mConnectThread;
	private int currentStatus = 0;
	private int warningStatus = 1;
	private int secondaryLevel = 2;
	private int thirdLevel = 4;
	private int normal = 0;
	public static final String BROADCAST_ACTION = "com.liteon.waterlevelalert.alertevent";
	private Intent intent;
	
	public void onCreate() {
		intent = new Intent(BROADCAST_ACTION);
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mConnectThread == null) {
			mConnectThread = new DataThread();
			mConnectThread.start();
		}
		Log.d(TAG, "onStartCommand");
		return 0;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mConnectThread.closeConnection();
		mConnectThread = null;
		
	}
	
	private class DataThread extends Thread {

		private SocketConnection mConnection;
		private Socket mSocket;
		private String serverIpAddress = "192.168.0.200";
		private static final int server_port = 502;
		boolean isEnable = false;
		private InetAddress serverAddr;
		private DataOutputStream dataOutputStream;
		private DataInputStream dataInputStream; 
		
		public DataThread() {
			mConnection = new SocketConnection(serverIpAddress, server_port);
		}
		
		@Override
		public void run() {
			super.run();
        	byte [] request_data = { 
        			//TCP HEADER -- 6 bytes
        			0x00, 0x01, 0x00, 0x00, 0x00, 0x06,
        			//Device ID -- 1 bytes
        			0x01,
        			//Function Code -- 1 bytes
        			0x01,
        			//Start Address -- 2 bytes
        			0x00,
        			0x01,
        			//No. of Register -- 2 bytes
        			0x00,
        			0x04
        			};
        	while(true) {
	            try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mConnection.connect();
				if (mConnection.isBroken()) {
					continue;
				}
				mConnection.sendCommand(request_data);
				int new_status = mConnection.getReply();
				if (new_status == -1) {
					Log.e(TAG, "Error reply");
					closeConnection();
					continue;
				}
				String current_level = getLevelString(currentStatus);
				String new_level = getLevelString(new_status);
				Log.d(TAG, "new status is " + new_level +", current status is " + current_level);
	            if (!current_level.equals(new_level)) {
	            	Uri rowUri = null;
	            	if (new_status != normal) {
	            		rowUri = insertDataToDB(new_level);
	            	}
	            	currentStatus = new_status;
	            	sendAlert(rowUri, new_level);
	            }

        	}
		}
		
		private Uri insertDataToDB(String waterLevel) {
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String date = sdFormat.format(new Date());
			ContentValues cv = new ContentValues();
			cv.put(AlertEntry.COLUMN_NAME_DATE, date);
			cv.put(AlertEntry.COLUMN_NAME_POLE_ID, "E"+new Date().getTime());
			cv.put(AlertEntry.COLUMN_NAME_ALERT_LEVEL, waterLevel);
			return getContentResolver().insert(AlertEntry.CONTENT_URI, cv);
		}
		
		private String getLevelString(int value) {
			if ((value & warningStatus) > 0) {
				return "Warning";
			} else if ((value & secondaryLevel) > 0) {
				return "Secondary Alert";
			} else if ((value & thirdLevel) > 0) {
				return "Thirdary Alert";
			} else if (value == normal) {
				return "normal";
			}
			return "";
		}
		
		public void closeConnection() {
			mConnection.disconnect();
		}
		
		public void sendAlert(Uri data, String waterlevel) {
			intent.putExtra("level", waterlevel);
			intent.putExtra("id", data);
			sendBroadcast(intent);
		}
	}
}
