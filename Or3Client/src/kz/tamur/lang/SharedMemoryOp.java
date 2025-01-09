package kz.tamur.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.or3ee.common.UserSession;

public class SharedMemoryOp {

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SharedMemoryOp.class.getName());

	private static final Map<String, Object> globalVars = new HashMap<>();
	
	public static Object getGlobalMap(String name) {
		synchronized (globalVars) {
			try {
				Map res = (Map)globalVars.get(name); 
				if (res == null) {
					res = Collections.synchronizedMap(new HashMap<Object, Object>());
					globalVars.put(name, res);
				}
				return res;
			} catch (Throwable e) {
				log.error(e, e);
				return null;
			}
		}
	}

	public static Object getUserMap(long userId, String name) {
		synchronized (globalVars) {
			try {
				Map res = (Map)globalVars.get(userId + "_" + name); 
				if (res == null) {
					res = Collections.synchronizedMap(new HashMap<Object, Object>());
					globalVars.put(userId + "_" + name, res);
				}
				return res;
			} catch (Throwable e) {
				log.error(e, e);
				return null;
			}
		}
	}
	
	public static Object removeMap(String name) {
		synchronized (globalVars) {
			try {
				return globalVars.remove(name); 
			} catch (Throwable e) {
				log.error(e, e);
				return null;
			}
		}
	}

	public static Object removeUserMap(long userId, String name) {
		synchronized (globalVars) {
			try {
				return globalVars.remove(userId + "_" + name); 
			} catch (Throwable e) {
				log.error(e, e);
				return null;
			}
		}
	}

	public static void removeUserMaps(long userId) {
		synchronized (globalVars) {
			try {
				List<String> keys = new ArrayList<>();
				for (String key : globalVars.keySet())
					if (key.startsWith(userId + "_"))
						keys.add(key);
				
				for (String key : keys)
					globalVars.remove(key);
				
			} catch (Throwable e) {
				log.error(e, e);
			}
		}
	}

	public static List<String> getKeys() {
		synchronized (globalVars) {
			try {
				List<String> keys = new ArrayList<>();
				for (String key : globalVars.keySet())
					keys.add(key);
				
				return keys;
				
			} catch (Throwable e) {
				log.error(e, e);
			}
		}
		return null;
	}

	public static List<String> getUserKeys(long userId) {
		synchronized (globalVars) {
			try {
				List<String> keys = new ArrayList<>();
				for (String key : globalVars.keySet())
					if (key.startsWith(userId + "_"))
						keys.add(key);
				
				return keys;
				
			} catch (Throwable e) {
				log.error(e, e);
			}
		}
		return null;
	}
}
