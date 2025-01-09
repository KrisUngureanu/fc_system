package kz.tamur.comps.models;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 17.09.2004
 * Time: 10:16:35
 * To change this template use File | Settings | File Templates.
 */
public class PropertyReestr {

    private static Map<String, PropertyNode> properties = new TreeMap<String, PropertyNode>();
    private static Map<String, PropertyNode> debugProperties = new TreeMap<String, PropertyNode>();
    private static Map<String, PropertyNode> expressionProperties = new TreeMap<String, PropertyNode>();
    private static Map<String, PropertyNode> filterProperties = new TreeMap<String, PropertyNode>();

    public static void registerProperty(PropertyNode pn) {
    	if (pn.getParent() instanceof ReportPrinterPropertyRoot && "title".equals(pn.getName())) {
    		String fullPath = pn.getFullPath() + ".reportPrinter";
    		if (!properties.containsKey(fullPath)) {
 	            properties.put(fullPath, pn);
 	        }
    	} else {
	        if (!properties.containsKey(pn.getFullPath())) {
	            properties.put(pn.getFullPath(), pn);
	        }
    	}
    }

    public static void unRegisterProperty(String path) {
        properties.remove(path);
    }

    public static void registerDebugProperty(PropertyNode pn) {
        if (debugProperties.containsKey(pn.toString())) {
            PropertyNode exPn = debugProperties.get(pn.toString());
            if (!exPn.getParent().equals(pn.getParent())) {
                debugProperties.put(pn.getParent().toString() + "." + pn.toString(), pn);
            }
        } else {
            debugProperties.put(pn.toString(), pn);
        }
    }
    
    public static void registerExpressionProperties(PropertyNode pn) {
        if (!expressionProperties.containsKey(pn.getFullPath())) {
        	expressionProperties.put(pn.getFullPath(), pn);
        }
    }

    public static List<PropertyNode> getRegisterProperties() {
        List<PropertyNode> res = new ArrayList<PropertyNode>();
        if (!properties.isEmpty()) {
            Iterator<String> it = properties.keySet().iterator();
            while(it.hasNext()) {
                res.add(properties.get(it.next()));
            }
        }
        return res;
    }

    public static List<PropertyNode> getDebugProperties() {
        List<PropertyNode> res = new ArrayList<PropertyNode>();
        if (!debugProperties.isEmpty()) {
            Iterator<String> it = debugProperties.keySet().iterator();
            while(it.hasNext()) {
                res.add(debugProperties.get(it.next()));
            }
        }
        return res;
    }
    
    public static List<PropertyNode> getExpressionProperties() {
        List<PropertyNode> res = new ArrayList<PropertyNode>();
        if (!expressionProperties.isEmpty()) {
            Iterator<String> it = expressionProperties.keySet().iterator();
            while(it.hasNext()) {
                res.add(expressionProperties.get(it.next()));
            }
        }
        return res;
    }
    
    public static void registerFilterProperty(PropertyNode pn) {
        if (!filterProperties.containsKey(pn.getFullPath())) {
        	filterProperties.put(pn.getFullPath(), pn);
        }
    }
    
    public static void unRegisterFilterProperty(String path) {
    	filterProperties.remove(path);
    }
    
    public static List<PropertyNode> getRegisterFilterProperties() {
        List<PropertyNode> res = new ArrayList<PropertyNode>();
        if (!filterProperties.isEmpty()) {
            Iterator<String> it = filterProperties.keySet().iterator();
            while(it.hasNext()) {
                res.add(filterProperties.get(it.next()));
            }
        }
        return res;
    }
}