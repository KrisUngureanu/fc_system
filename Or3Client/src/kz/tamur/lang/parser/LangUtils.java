package kz.tamur.lang.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

public class LangUtils {
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + LangUtils.class.getName());
	private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private static List<String> nullClasses = new ArrayList<String>();
	
    public static Class<?> getType(String name, String hash) {
    	return getType(name, hash, null, null);
    }
    
    public static Class<?> getType(String name, String hash, ClassLoader cl, Class jarClass) {
    	if ("String".equals(name)) {
    		return String.class;
    	} else if ("System".equals(name)) {
    		return System.class;
    	} else if ("Runtime".equals(name)) {
    		return Runtime.class;
    	} else if ("Math".equals(name)) {
    		return Math.class;
    	}
    	
    	if (nullClasses.contains(name)) return null;
    	Class<?> res = classes.get(name);
    	if (res == null) {
	    	try {
				res = safeLoadClass(name, hash, cl, jarClass);
				classes.put(name, res);
			} catch (ClassNotFoundException e) {
				log.warn(e.getMessage());
				nullClasses.add(name);
			}
    	}
    	return res;
    }
    
	private static Class<?> safeLoadClass(String className, String hash, ClassLoader cl, Class jarClass) throws ClassNotFoundException {
	    String classHash = calculateHash(className, jarClass);
	    if (classHash.equals(hash) || hash == null) {
	    	className = Funcs.validate(Funcs.normalizeInput(className));
			Class<?> cls = cl == null ? Class.forName(className) : Class.forName(className, true, cl);
	    	return cls;
	    } else 
	    	throw new ClassNotFoundException("Class hash of '" + className + "' - " + classHash + " is not equal to " + hash);
	}
	
	private static String calculateHash(String clsName, Class jarClass) throws ClassNotFoundException {
	    String path = "/" + clsName.replace('.', '/') + ".class";
	    InputStream is = (jarClass != null) ?
	    		jarClass.getResourceAsStream(path) :
	    		LangUtils.class.getResourceAsStream(path);
	    
	    if (is == null)
	    	throw new ClassNotFoundException("Class resource with path '" + path + "' not found!!!");
	    byte[] classBytes;
		try {
			classBytes = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
		    String res = Utils.getHash(classBytes);
		    
		    log.info("Hash of '" + clsName + "' = " + res);
		    return res;
		} catch (IOException e) {
	    	throw new ClassNotFoundException(e.getMessage());
		}
	}
}
