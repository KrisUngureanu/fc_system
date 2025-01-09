package com.cifs.or2.kernel;

import java.util.Date;

public class ReplChangesProgressNote extends Note {
	
	public final int type;
	public final int currentChangeNumber;
	public final int changesCount;
	public final String changeType;
	public final String changeId;

	public ReplChangesProgressNote(Date time, UserSessionValue from, int type, int currentChangeNumber, int changesCount, String changeType, String changeId) {
		super(time, from);
		this.type = type;
		this.currentChangeNumber = currentChangeNumber;
		this.changesCount = changesCount;
		this.changeType = changeType;
		this.changeId = changeId;
	}
}