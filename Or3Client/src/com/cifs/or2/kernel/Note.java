package com.cifs.or2.kernel;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
	
	public final Date time;
	public final UserSessionValue from;

	public Note(Date time, UserSessionValue from) {
		super();
		this.time = time;
		this.from = from;
	}

}
