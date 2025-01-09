package kz.tamur.web.common;

import java.util.Map;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 16.06.2006
 * Time: 11:24:50
 * To change this template use File | Settings | File Templates.
 */
public class ServletUtilities {
    public static final String DOCTYPE = "<!DOCTYPE>";

    public static final String DOCTYPE_JQ = "<!DOCTYPE HTML>";

    public static final String EOL = Constants.EOL;
    
    public static int getInt(String key, Map<String, String> args, int defaultValue) {
    	String s = args.get(key);
    	if (s != null && s.length() > 0) {
    		try {
    			int res = Integer.parseInt(s);
    			return res;
    		} catch (Exception e) {}
    	}
    	return defaultValue;
    }
}
