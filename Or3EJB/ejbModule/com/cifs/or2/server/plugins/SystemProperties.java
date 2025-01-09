package com.cifs.or2.server.plugins;

import java.util.Properties;

public class SystemProperties {
	public static int maxThreadCount = Integer.parseInt(System.getProperty("maxThreadCount", "10"));
	public static int maxFlowCount = Integer.parseInt(System.getProperty("maxFlowCount", "500"));
	
	public static String log4jPath = System.getProperty("or3.log4j.configuration", System.getProperty("log4j.configuration")); 

	public static Properties getProperties() {
		return System.getProperties();
	}
	
	public static String getProperty(String key) {
		return System.getProperty(key);
	}

	public static String getProperty(String key, String defVal) {
		return System.getProperty(key, defVal);
	}

	public static void setProperty(String key, String val) {
		System.setProperty(key, val);
		if ("maxThreadCount".equals(key))
			maxThreadCount = Integer.parseInt(val);
		else if ("maxFlowCount".equals(key))
			maxFlowCount = Integer.parseInt(val);
		
	}
}
