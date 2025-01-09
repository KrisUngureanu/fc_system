package kz.tamur.common;

import java.util.HashMap;
import java.util.Map;

public class Global {

	public Global() {
	}
	
	public static Map<String, Object> globalVars = java.util.Collections.synchronizedMap(new HashMap<String, Object>());
	
	public static Map<String, Object> getGlobalVars() {
		return globalVars;
	}

	public static Object getGlobalVar(String name) {
		return globalVars.get(name);
	}
	
	public static void setGlobalVar(String name, Object val) {
		globalVars.put(name, val);
	}
}
