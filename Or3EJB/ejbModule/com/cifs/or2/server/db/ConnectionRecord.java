package com.cifs.or2.server.db;

import java.lang.ref.WeakReference;
import java.sql.Connection;

public final class ConnectionRecord {

	private StackTraceElement[] createTrace;
	private WeakReference<Connection> conRef;
	
	public ConnectionRecord(Connection conn) {
		createTrace = Thread.currentThread().getStackTrace();
		conRef = new WeakReference<Connection>(conn);
	}
	
	public Connection getConnection() {
		return conRef.get();
	}
	
	public StackTraceElement[] getCreateTrace() {
		return createTrace;
	}
}
