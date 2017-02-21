package com.liteon.waterlevelalert.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;
import com.liteon.waterlevelalert.util.Def;
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
	private Intent intent;
	
	public void onCreate() {
		intent = new Intent(Def.BROADCAST_ACTION);
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mConnectThread == null) {
			mConnectThread = new DataThread();
			mConnectThread.start();
		}
		String action = intent.getAction();
		if (action != null && action.equals(Def.BROADCAST_ACTION_GET_LEVEL)){
			sendAlert(null, getLevelString(currentStatus));
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
			return Def.WARNING_ALERT;
		} else if ((value & secondaryLevel) > 0) {
			return Def.SECONDARY_ALERT;
		} else if ((value & thirdLevel) > 0) {
			return Def.THIRDARY_ALERT;
		} else if (value == normal) {
			return Def.NORMAL;
		}
		return "";
	}
	
	private void sendAlert(Uri data, String waterlevel) {
		intent.putExtra(Def.LEVEL, waterlevel);
		intent.putExtra(Def.NEW_ALERT_DATA_URI, data);
		sendBroadcast(intent);
		Log.d(TAG, "sendAlert Uri : " + data + ", alert is " + waterlevel);
	}
	
	private class DataThread extends Thread {

		private SocketConnection mConnection;
		private Socket mSocket;
		private String serverIpAddress = Def.URL;
		private static final int server_port = Def.PORT;
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
				mConnection.sendCommand(Def.MODBUS_REQUEST_DATA);
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
	
		public void closeConnection() {
			mConnection.disconnect();
		}
	}
}
