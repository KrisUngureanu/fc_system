package com.cifs.or2.kernel;

import java.util.Date;

public class ReportNote extends Note {
	
	private String reportId;
	private String cmd;
	private String param;
	
	public ReportNote(Date time, UserSessionValue from, String reportId, String cmd, String param) {
		super(time, from);
		this.reportId = reportId;
		this.cmd = cmd;
		this.param = param;
	}

	public String getReportId() {
		return reportId;
	}

	public String getCmd() {
		return cmd;
	}

	public String getStringParam() {
		return (String) param;
	}

	public int getIntParam() {
		return Integer.parseInt(param);
	}

	public long getLongParam() {
		return Long.parseLong(param);
	}
}
