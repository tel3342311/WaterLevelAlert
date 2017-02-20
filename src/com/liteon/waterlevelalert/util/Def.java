package com.liteon.waterlevelalert.util;

public class Def {

	public final static String URL = "192.168.0.200";
	public final static int PORT = 502;
	public final static String BROADCAST_ACTION = "com.liteon.waterlevelalert.alertevent";
	public final static String BROADCAST_ACTION_GET_LEVEL = "com.liteon.waterlevelalert.currentLevel";
	public final static String LEVEL = "level";	
	public final static String WARNING_ALERT = "Warning";
	public final static String SECONDARY_ALERT = "Secondary alert";
	public final static String THIRDARY_ALERT = "Thirdary alert";
	public final static String NORMAL = "Normal";
	public final static String NEW_ALERT_DATA_URI = "com.liteon.waterlevelalert.new_data_uri";
	public final static String AUTHORITY = "com.liteon.waterlevelalert";
	public final static int URI_ALERT_DATA_ID = 1;
	public final static int URI_ALERT_DATAS = 2;
	public final static byte [] MODBUS_REQUEST_DATA = { 
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
}
