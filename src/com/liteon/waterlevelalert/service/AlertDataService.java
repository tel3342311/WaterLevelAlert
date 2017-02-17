package com.liteon.waterlevelalert.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;
import com.liteon.waterlevelalert.util.SocketConnection;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlertDataService extends Service {

	private static final String TAG = AlertDataService.class.getName();
	private static DataThread mConnectThread;
	private int currentStatus = 0;
	private int warningStatus = 7;
	private int secondaryLevel = 6;
	private int thirdLevel = 4;
	private int normal = 0;
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
				Log.d(TAG, "new status is " + getLevelString(new_status) +", current status is " + getLevelString(currentStatus));
	            if (new_status != currentStatus) {
	            	if (new_status != normal) {
	            		insertDataToDB(new_status);
	            	}
	            	currentStatus = new_status;
	            }

        	}
		}
		
		
		
		private void insertDataToDB(int waterLevel) {
			if (getLevelString(waterLevel).isEmpty()) {
				return;
			}
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String date = sdFormat.format(new Date());
			ContentValues cv = new ContentValues();
			cv.put(AlertEntry.COLUMN_NAME_DATE, date);
			cv.put(AlertEntry.COLUMN_NAME_POLE_ID, "E"+new Date().getTime());
			cv.put(AlertEntry.COLUMN_NAME_ALERT_LEVEL, getLevelString(waterLevel));
			getContentResolver().insert(AlertEntry.CONTENT_URI, cv);
		}
		
		private String getLevelString(int value) {
			if (value == warningStatus) {
				return "Warning";
			} else if (value == secondaryLevel) {
				return "Secondary Alert";
			} else if (value == thirdLevel) {
				return "thirdary Alert";
			} else if (value == normal) {
				return "normal";
			}
			return "";
		}
		
		public void closeConnection() {
			mConnection.disconnect();
		}
	}
}
