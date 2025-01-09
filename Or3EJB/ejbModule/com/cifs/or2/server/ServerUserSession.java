package com.cifs.or2.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.AttrChangeNote;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

public class ServerUserSession extends UserSession implements Serializable, AttrChangeListener {
	
	private static final Log LOG = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ServerUserSession.class);

	private static final long serialVersionUID = 1L;

	transient private Map<UUID, List<AttrChange>> attrChanges = new HashMap<UUID, List<AttrChange>>();
	
    private UUID id;
    private UserSrv user;
    private long[] baseIds = new long[0];
    
    transient private Set<Pair<Long, Long>> cachedUnlocks = new HashSet<Pair<Long,Long>>();
    transient private Map<String, Map<String, List<Object>>> filterParams =
    		new HashMap<String, Map<String, List<Object>>>();
    
    transient private Session s;
    transient private byte[] photo;
    private transient List<File> files = new ArrayList<File>();

    public ServerUserSession(
    		String dsName,
    		String typeClient,
    		UserSrv user,
    		String ip,
    		String pcName,
    		String serverId,
    		boolean callbakcs) {
    	this.user = user;
    	this.id = UUID.randomUUID();
    	this.dsName = Funcs.normalizeInput(dsName);
    	this.typeClient = Funcs.normalizeInput(typeClient);
        this.ip = Funcs.normalizeInput(ip);
        this.pcName = Funcs.normalizeInput(pcName);
        this.serverId = serverId;
        this.callbacks = callbakcs;
    	this.baseObj = user != null ? user.getBaseObj() : null;
    	this.balansObj = user != null ? user.getBalansObj() : null;
    	this.userObj = user != null ? user.getUserObj() : null;
    	this.intLangObj = user != null ? user.getInterfaceLang() : null;
    	this.dataLangObj = user != null ? user.getDataLang() : null;
    	this.name = user != null ? Funcs.normalizeInput(user.getName()) : null;
    	this.iin = user != null ? user.getIin() : null;
    	this.logName = (name == null) ? "sys" : name.replaceAll("\\s|\\.", "_");
    	this.isAdmin = user != null ? user.isAdmin() : true;
    	this.isMulti = user != null ? user.isMulti() : true;
    	
    	if (callbacks) LOG.info("Created session id: " + id + ", name: " + name + ", lastPing: " + lastPing);
	}

	@Override
	public UUID getId() {
		return id;
	}

    public void logLoginTime(Session s) throws KrnException {
        KrnClass userCls = s.getClassByName("User");
        KrnAttribute timeAttr = s.getAttributeByName(userCls, "lastLoginTime");
        // Тут надо разобраться подробнее
        KrnAttribute isLogged = s.getAttributeByName(userCls, "isLogged");
        if (timeAttr != null && isLogged != null) {
            s.setValue(userObj.id, isLogged.id, 0, 1, true, 0, false);
            s.setValue(userObj.id, timeAttr.id, 0, 0, new Timestamp(System.currentTimeMillis()), 0, false);
        } else {
            kz.tamur.rt.Utils.outErrorCreateAttrUser("lastLoginTime, isLogged");
        }
    }
 
    public void logLogoutTime(Session s) {
    	try { 
			KrnClass userCls = s.getClassByName("User");
			KrnAttribute lastLogoutTime = s.getAttributeByName(userCls,"lastLogoutTime");
	    	if (lastLogoutTime != null) {
	    		s.setTime(s.getUser().id, lastLogoutTime.id, 0, Funcs.convertTime(new java.util.Date()), 0);
	    		s.commitTransaction();
	    	}
		} catch (KrnException e) {
			e.printStackTrace();
		}
    }
    
    public UserSrv getUser() {
    	return user;
    }

    public long[] getBaseIds() {
		return baseIds != null ? Arrays.copyOf(baseIds, baseIds.length) : null;
    }

    public void setBaseIds(long[] baseIds) {
        this.baseIds = baseIds != null ? Arrays.copyOf(baseIds, baseIds.length) : null;
    }
    
    public void cachedUnlock(long objId, long processId){
    	cachedUnlocks.add(new Pair<Long, Long>(objId, processId));
    }

    public boolean cachedLock(long objId, long processId){
		return cachedUnlocks.remove(new Pair<Long, Long>(objId, processId));
    }
    
    public boolean cachedUnlocked(long objId, long processId){
		return cachedUnlocks.contains(new Pair<Long, Long>(objId, processId));
    }

    public void cachedClear() {
    	cachedUnlocks.clear();
    }
    
    public Set<Pair<Long, Long>> getCachedUnlocks() {
    	return Collections.unmodifiableSet(cachedUnlocks);
    }

    public Map<String, List<Object>> getFilterParams(String fuid) {
    	return filterParams.get(fuid);
    }

    public boolean setFilterParam(String fuid, String pid, List<Object> values) {
        Map<String, List<Object>> map = filterParams.get(fuid);
        if (map == null) {
            map = new HashMap<String, List<Object>>();
            synchronized (filterParams) {
                filterParams.put(fuid, map);
			}
        }
        if (values != null) {
        	List<Object> old = map.get(pid);
            map.put(pid, values);
        	if (old == null) return true;
        	else if (old.size() != values.size()) return true;
        	else {
        		for (int i=0; i<old.size(); i++) {
        			if (old.get(i) == null && values.get(i) != null) return true;
        			if (old.get(i) != null && values.get(i) == null) return true;
        			if (old.get(i) != null && values.get(i) != null && !old.get(i).equals(values.get(i))) return true;
        		}
        	}
        } else {
        	List<Object> old = map.remove(pid);
        	if (old != null) return true;
        }
        return false;
    }

    public boolean clearFilterParams(String fuid) {
    	Map<String, List<Object>> map = filterParams.get(fuid);
    	if (map != null && map.size() > 0) {
    		map.clear();
    		return true;
    	}
    	return false;
    }

	public Session getSession() {
		return s;
	}

	public void setSession(Session s) {
		this.s = s;
	}
	
	@Override
	public void attrChanged(KrnObject obj, long attrId, long langId, long trId, UUID uuid) {
		List<AttrChange> l = attrChanges.get(uuid);
		if (l == null) {
			l = new ArrayList<AttrChange>();
			attrChanges.put(uuid, l);
		}
		l.add(new AttrChange(obj, attrId, langId, trId));
	}

	@Override
	public void commit(UUID uuid) {
		List<AttrChange> l = attrChanges.remove(uuid);
		if (l != null && l.size() > 0) {
			ServerUserSession us = Session.findUserSession(uuid);
			if (us != null) {
				UserSessionValue usv = Session.createValueObject(us);
				for (AttrChange a : l) {
					addNote(new AttrChangeNote(new Date(), usv, a.obj, a.attrId, a.langId, a.trId));
				}
			}
		}
	}

	@Override
	public void rollback(UUID uuid) {
		attrChanges.remove(uuid);
	}


	@Override
	public void commitLongTransaction(UUID uuid, long trId) {
	}

	@Override
	public void rollbackLongTransaction(UUID uuid, long trId) {
	}

	public void setPhoto(byte[] foto) {
		this.photo = foto != null ? Arrays.copyOf(foto, foto.length) : null;
	}
	
	public byte[] getPhoto() {
		return photo != null ? Arrays.copyOf(photo, photo.length) : null;
	}
	
	private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
		is.defaultReadObject();
		
		attrChanges = new HashMap<UUID, List<AttrChange>>();
		cachedUnlocks = new HashSet<Pair<Long,Long>>();
		filterParams = new HashMap<String, Map<String, List<Object>>>();
	}
	
	public void cleanTempFiles() {
		LOG.info("cleaning session: " + id);
        for (int i = files.size() - 1; i>=0; i--) {
        	File f = files.get(i);
        	try {
        		if (f != null) {
        			LOG.info("deleting file after session close: " + f.getAbsolutePath());
	        		f.delete();
        		}
        	} catch (Exception e) {}
        	f = null;
        }
        files.clear();
    }
    
	public void deleteFileOnExit(File f) {
		LOG.info("adding file " + ((f != null) ? f.getAbsolutePath() : null) + " to session: " + id);
		files.add(f);
	}
}
