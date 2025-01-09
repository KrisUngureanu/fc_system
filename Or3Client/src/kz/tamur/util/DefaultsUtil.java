package kz.tamur.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultsUtil {
	
	private static ResourceBundle rb = null;
	private static Log log = LogFactory.getLog(DefaultsUtil.class);
	
	static {
		try {
			rb = ResourceBundle.getBundle("defaults");
		} catch (MissingResourceException e) {
			log.warn("Not found file: defaults.properties");
		}
	}
	
	public static String getStringDefaultValue(String name, String def) {
		if (rb != null)
			return rb.getString(name);
		return def;
	}
	
	public static Boolean getBooleanDefaultValue(String name, Boolean def) {
		if (rb != null) {
			String v = rb.getString(name);
			if (v != null) {
				return v.equals("true") || v.equals("1");
			}
		}
		return def;
	}

}
