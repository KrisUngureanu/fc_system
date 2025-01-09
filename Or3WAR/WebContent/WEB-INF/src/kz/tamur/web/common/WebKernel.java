package kz.tamur.web.common;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class WebKernel extends Kernel {

    /**
     * Для кэширования объектов по UID
     */
    protected static Map<Integer, Map<String, KrnObject>> multiObjByUid =
    	new WeakHashMap<Integer, Map<String, KrnObject>>();

    /**
     * Для кэширования объектов по ID
     */
    protected static Map<Integer, Map<Long, KrnObject>> multiObjById =
    	new WeakHashMap<Integer, Map<Long, KrnObject>>();

    private int configNumber = 0;

	public WebKernel(int configNumber) {
        super();
        this.configNumber = configNumber;
    }

    public Thread getCallback() {
    	if (callback == null)
            callback = new WebClientCallback(this);
    	return callback;
    }

    @Override
    public boolean isSE_UI() {
        return seUI;
    }

    @Override
    public boolean isADVANCED_UI() {
        return advancedUI;
    }
    
    protected void cacheObject(KrnObject obj) {
    	Map<Long, KrnObject> idMap = null;

    	synchronized (multiObjById) {
    		idMap = multiObjById.get(configNumber);
    		if (idMap == null) {
    			idMap = new WeakHashMap<Long, KrnObject>();
    			multiObjById.put(configNumber, idMap);
    		}
    	}

    	synchronized (idMap) {
    		idMap.put(obj.id, obj);
    	}

    	Map<String, KrnObject> uidMap = null;
    	
    	synchronized (multiObjByUid) {
    		uidMap = multiObjByUid.get(configNumber);
    		if (uidMap == null) {
    			uidMap = new WeakHashMap<String, KrnObject>();
    			multiObjByUid.put(configNumber, uidMap);
    		}
    	}

    	synchronized (uidMap) {
    		uidMap.put(obj.uid, obj);
    	}
    }
    
    public KrnObject getCachedObjectByUid(String uid) throws KrnException {
    	Map<String, KrnObject> uidMap = null;
    	
    	synchronized (multiObjByUid) {
    		uidMap = multiObjByUid.get(configNumber);
    		if (uidMap == null) {
    			uidMap = new WeakHashMap<String, KrnObject>();
    			multiObjByUid.put(configNumber, uidMap);
    		}
    	}

    	synchronized (uidMap) {
    		if (uidMap.containsKey(uid))
    			return uidMap.get(uid);
    		KrnObject obj = getObjectByUid(uid, -1);
    		if (obj == null) {
    			uidMap.put(uid, null);
    		} else {
    			cacheObject(obj);
    			return obj;
    		}
		}
    	return null;
    }
    
    public KrnObject getCachedObjectById(Long id) throws KrnException {
    	Map<Long, KrnObject> idMap = null;

    	synchronized (multiObjById) {
    		idMap = multiObjById.get(configNumber);
    		if (idMap == null) {
    			idMap = new WeakHashMap<Long, KrnObject>();
    			multiObjById.put(configNumber, idMap);
    		}
    	}

    	synchronized (idMap) {
    		if (idMap.containsKey(id))
    			return idMap.get(id);
    		KrnObject obj = getObjectById(id, -1);
    		if (obj == null) {
    			idMap.put(id, null);
    		} else {
    			cacheObject(obj);
    			return obj;
    		}
		}
    	return null;
    }
    
    public void addToCache(Set<String> uids) throws KrnException {
    	Map<String, KrnObject> uidMap = null;
    	
    	synchronized (multiObjByUid) {
    		uidMap = multiObjByUid.get(configNumber);
    		if (uidMap == null) {
    			uidMap = new WeakHashMap<String, KrnObject>();
    			multiObjByUid.put(configNumber, uidMap);
    		}
    	}

    	synchronized (uidMap) {
	    	uids.removeAll(uidMap.keySet());
	    	if (uids.size() > 0) {
	    		KrnObject[] objs = getObjectsByUid(uids.toArray(new String[uids.size()]), 0);
	    		for (KrnObject obj : objs)
	    			cacheObject(obj);
	    	}
    	}
    }
}
