package com.cifs.or2.kernel;

import java.util.Date;

import com.eclipsesource.json.JsonArray;

public class DeleteNotificationsNote extends Note {
	
	private JsonArray objIds;
	
	public DeleteNotificationsNote(Date time, UserSessionValue from, JsonArray objIds) {
		super(time, from);
		this.objIds = objIds;
	}
	
	public JsonArray getObjIds() {
		return objIds;
	}
	
}
