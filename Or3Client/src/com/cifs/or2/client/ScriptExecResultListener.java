package com.cifs.or2.client;

import java.util.Map;

public interface ScriptExecResultListener {
	void scriptExecResult(int resultCode, Map<String, Object> varsMap, String message);
}