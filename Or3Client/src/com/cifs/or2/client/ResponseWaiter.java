package com.cifs.or2.client;

public interface ResponseWaiter {
	void responseRecieved(String response);
	String getReportId();
}