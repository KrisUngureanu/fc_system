package com.cifs.or2.kernel;

import java.util.Date;
import java.util.Map;

public class ScriptExecResultNote extends Note {
	
	public int resultCode;
	public Map<String, Object> varsMap;
	public String message;
	
	public ScriptExecResultNote(Date time, UserSessionValue from, int resultCode, Map<String, Object> varsMap, String message) {
		super(time, from);
		this.resultCode = resultCode;
		this.varsMap = varsMap;
		this.message = message;
	}
}