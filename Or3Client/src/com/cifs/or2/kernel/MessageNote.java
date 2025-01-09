package com.cifs.or2.kernel;

import java.util.Date;

public class MessageNote extends Note {
	
	public final String message;
	public final boolean isDropUser;

	public MessageNote(Date time, UserSessionValue from, String message) {
		super(time, from);
		this.message = message;
		this.isDropUser = false;
	}
	
	public MessageNote(Date time, UserSessionValue from, String message, boolean isDropUser) {
		super(time, from);
		this.message = message;
		this.isDropUser = isDropUser;
	}
}