package com.liteon.waterlevelalert.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liteon.waterlevelalert.util.AlertDataDBHelper;
import com.liteon.waterlevelalert.util.AlertTable.AlertEntry;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera.Size;
import android.net.wifi.WifiConfiguration.Status;
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
		
		private Socket mSocket;
		private String serverIpAddress = "192.168.0.200";
		private static final int server_port = 502;
		boolean isEnable = false;
		private InetAddress serverAddr;
		private DataOutputStream dataOutputStream;
		private DataInputStream dataInputStream; 
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
        	
			try {
				Log.d(TAG, "run() start");
				openConnection();
				
				dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
				dataInputStream = new DataInputStream(mSocket.getInputStream());
		        while (isEnable) {
					Log.d(TAG, "run() isEnable : " + isEnable);
		        	dataOutputStream.write(request_data);
		        	
		        	int count = dataInputStream.available();
		            
		            // create buffer
		            byte[] bs = new byte[count];
		            
		            // read data into buffer
		            dataInputStream.read(bs);
		            
		            Log.d(TAG, "byte count " + count);
		            int TCP_HEADER_SIZE = 6; 
		            if ( count > TCP_HEADER_SIZE) {
			            int data_size = bs[TCP_HEADER_SIZE - 1];
			            int value = bs[TCP_HEADER_SIZE + data_size -1];
			            int new_status = value;
			            Log.d(TAG, "current status is " + getLevelString(currentStatus));
			            Log.d(TAG, "new status is " + getLevelString(new_status));
			            if (new_status != currentStatus) {
			            	if (new_status != normal) {
			            		insertDataToDB(new_status);
			            	}
			            	currentStatus = new_status;
			            }
		            }
			        Thread.sleep(2000);
		        }
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}			
		}
		
		public void openConnection() {
			try {
				serverAddr = InetAddress.getByName(serverIpAddress);
				mSocket = new Socket(serverAddr, server_port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (mSocket != null) {
				isEnable = (!mSocket.isClosed() && mSocket.isConnected());
			} else { 
				isEnable = false;
			}
			Log.d(TAG, "open connection: isEnable = " + isEnable);
		} 
		
		public void closeConnection() {
			isEnable = false;
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				if (dataInputStream != null) {
					dataInputStream.close();
				}
				if (mSocket != null) {
					mSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "close connection: isEnable = " + isEnable);
		}
		
		public boolean isEnable() {
			return isEnable;
		}
		
		private void insertDataToDB(int waterLevel) {
			
			SQLiteDatabase db = AlertDataDBHelper.getInstance(getApplicationContext()).getWritableDatabase();
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String date = sdFormat.format(new Date());
			ContentValues cv = new ContentValues();
			cv.put(AlertEntry.COLUMN_NAME_DATE, date);
			cv.put(AlertEntry.COLUMN_NAME_POLE_ID, "E222123");
			cv.put(AlertEntry.COLUMN_NAME_ALERT_LEVEL, getLevelString(waterLevel));
			long num = db.insert(AlertEntry.TABLE_NAME, null, cv);
			db.close();
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
		
		@Override
		protected void finalize() throws Throwable {
			try{
				if (mSocket != null)
				mSocket.close();
		    } catch (IOException e) {
		        System.out.println("Could not close socket");
		        System.exit(-1);
		    }
			super.finalize();
		}
	}
}
