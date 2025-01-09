package com.cifs.or2.server;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.common.UserSession;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.06.2005
 * Time: 11:08:11
 * To change this template use File | Settings | File Templates.
 */
public class GlobalFuncs {

    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + GlobalFuncs.class.getName());

    private static Map<String, Map<String, String>> textByName = new HashMap<String, Map<String, String>>();
    private static Map<String, Map<Long, String>> nameById = new HashMap<String, Map<Long, String>>();

    public synchronized String getText(String funcName, Session s) {
    	String dsName = s.getDsName();
		Map<String, String> map = textByName.get(dsName);
		Map<Long, String> map2 = nameById.get(dsName);
    	if (map == null) {
    		map = new HashMap<String, String>();
    		textByName.put(dsName, map);
    		map2 = new HashMap<Long, String>();
    		nameById.put(dsName, map2);
    	}
    	String text = map.get(funcName);
        if (text == null) {
            try {
                KrnClass cls = s.getClassByName("Func");
                KrnAttribute nameAttr = s.getAttributeByName(cls, "name");
                KrnAttribute textAttr = s.getAttributeByName(cls, "text");
                KrnObject[] objs = s.getObjectsByAttribute(
                        cls.id, nameAttr.id, 0, ComparisonOperations.CO_EQUALS, funcName, 0);
                if (objs.length > 0) {
                    byte[] data = s.getBlob(objs[0].id, textAttr.id, 0, 0, 0);
                    if (data.length > 0) {
                        text = new String(data, "UTF-8");
                        map.put(funcName, text);
                        map2.put(new Long(objs[0].id), funcName);
                    }
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        return text;
    }

    public synchronized void updateText(long objId, byte[] data, Session s) {
    	String dsName = s.getDsName();
		Map<String, String> map = textByName.get(dsName);
		if (map != null) {
			Map<Long, String> map2 = nameById.get(dsName);
	        String name = map2.get(objId);
	        if (name != null) {
	            String text = "";
	            
	            try {
	                if (data.length > 0) {
	                    text = new String(data, "UTF-8");
	                }
	            } catch (UnsupportedEncodingException e) {
	                log.error(e, e);
	                text = null;
	            }
	            if (text != null) {
	                map.put(name, text);
	            } else {
	                map.remove(name);
	            }
	        }
		}
    }
}
