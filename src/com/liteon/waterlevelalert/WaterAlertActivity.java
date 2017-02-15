package com.liteon.waterlevelalert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WaterAlertActivity extends Activity {
	
	private Socket socket;
	private String serverIpAddress = "192.168.0.200";
	private static final int server_port = 502;

	private static String message;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_water_alert);

		Thread thread = new Thread(Connection);
		thread.start();
		
	}
	
	private Runnable Connection = new Runnable() {

		@Override
		public void run() {
			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName(serverIpAddress);
		        socket = new Socket(serverAddr, server_port);
		        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		        Log.d("socket", "socket connected : " + socket.isConnected());
		        while (socket.isConnected()) {
		        	byte [] data = {0x01, 0x01};
		        	dataOutputStream.write(data);
		        	
		        	int count = dataInputStream.available();
		            
		            // create buffer
		            byte[] bs = new byte[count];
		            
		            // read data into buffer
		            dataInputStream.read(bs);
		            
		            Log.d("Socket", "byte count " + count);
		            for (byte bb:bs)
		            {
		               // convert byte into character
		               int c = (int)bb;
		               Log.d("socket", "output: " + c);
		            }
			        Thread.sleep(2000);
		        }
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	};
	@Override
	protected void onDestroy() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getCRC(byte[] buf, int len ) {
	    int crc =  0xFFFF;
	    int val = 0;

	      for (int pos = 0; pos < len; pos++) {
	        crc ^= (int)(0x00ff & buf[pos]);  // FIX HERE -- XOR byte into least sig. byte of crc

	        for (int i = 8; i != 0; i--) {    // Loop over each bit
	          if ((crc & 0x0001) != 0) {      // If the LSB is set
	            crc >>= 1;                    // Shift right and XOR 0xA001
	            crc ^= 0xA001;
	          }
	          else                            // Else LSB is not set
	            crc >>= 1;                    // Just shift right
	        }
	      }
	    // Note, crc has low and high bytes swapped, so use it accordingly (or swap bytes)
	    val =  (crc & 0xff) << 8;
	    val =  val + ((crc >> 8) & 0xff);
	    System.out.printf("Calculated a CRC of 0x%x, swapped: 0x%x\n", crc, val);
	    return val;  

	} 
}
