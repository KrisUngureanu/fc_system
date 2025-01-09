package com.cifs.or2.kernel;

import java.util.Date;

public class NotificationNote extends Note {
	
	public final long objId;
	public final String message;
	public final String uid;
	public final String cuid;
	public final String proc;
	public final String iter;


	public NotificationNote(Date time, UserSessionValue from, long objId, String message, String uid, String cuid, String proc, String iter) {
		super(time, from);
		this.objId = objId;
		this.message = message;
		this.uid = uid;
		this.cuid = cuid;
		this.proc = proc;
		this.iter = iter;

	}	
}