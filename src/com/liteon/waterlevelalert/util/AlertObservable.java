package com.liteon.waterlevelalert.util;

import java.util.Observable;

public class AlertObservable extends Observable {
	
	
	private String status;
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String newStatus) {
		if (!status.equals(newStatus)) {
			status = newStatus;
		}
	}
}
