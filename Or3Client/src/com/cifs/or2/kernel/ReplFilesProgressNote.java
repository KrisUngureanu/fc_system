package com.cifs.or2.kernel;

import java.util.Date;

public class ReplFilesProgressNote extends Note {
	
	public final int type;
	public final int filesCount;
	public final int currentFileNumber;
	public final String currentFileName;
	public final Time importTime;

	public ReplFilesProgressNote(Date time, UserSessionValue from, int type, int filesCount, int currentFileNumber, String currentFileName, Time importTime) {
		super(time, from);
		this.type = type;
		this.filesCount = filesCount;
		this.currentFileNumber = currentFileNumber;
		this.currentFileName = currentFileName;
		this.importTime = importTime;
	}
}