package com.liteon.waterlevelalert.util;

import android.net.Uri;
import android.provider.BaseColumns;

public class AlertTable {

	public AlertTable() {}
	public static final String AUTHORITY = "com.liteon.waterlevelalert";
	
	public static abstract class AlertEntry implements BaseColumns {
		public static final String PATH_MULTIPLE = "objs"; 
		public static final String TABLE_NAME = "alert_entry";
		public static final Uri CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + PATH_MULTIPLE);
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_POLE_ID = "pole_id";
        public static final String COLUMN_NAME_ALERT_LEVEL = "alert_level";
	}
	
}
