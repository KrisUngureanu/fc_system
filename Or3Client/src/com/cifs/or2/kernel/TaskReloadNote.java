package com.cifs.or2.kernel;

import java.util.Date;

public class TaskReloadNote extends Note {
	
	public final long flowId;
	public final long flowParam;

	public TaskReloadNote(Date time, UserSessionValue from, long flowId, long flowParam) {
		super(time, from);
		this.flowId = flowId;
		this.flowParam = flowParam;
	}

}
