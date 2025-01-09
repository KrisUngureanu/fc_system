package com.cifs.or2.server.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;

import kz.tamur.ods.Driver2;
import kz.tamur.or3.util.ProtocolRule;
import kz.tamur.or3.util.SystemRight;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.CacheListener;
import kz.tamur.server.wf.ExecutionEngine;

public class DatabaseCacheListener implements CacheListener<Object, Object> {
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + DatabaseCacheListener.class.getName());
	private String dsName;
	
	public DatabaseCacheListener(String dsName) {
		this.dsName = dsName;
	}

	@Override
	public void entryAdded(String cacheName, Object key, Object value, Object oldValue) {
		entryUpdated(cacheName, key, value, oldValue);
	}

	@Override
	public void entryUpdated(String cacheName, Object key, Object value, Object oldValue) {
		if ("classCache".equals(cacheName)) {
			KrnClass cls = (KrnClass)value;
			Database.classesByName.put(cls.name, cls);
			Database.classesById.put(cls.id, cls);
			Database.classesByUid.put(cls.uid, cls);
			
    		List<KrnClass> list = null;
    		synchronized (Database.classesByParentId) {
	    		list = Database.classesByParentId.get(cls.parentId);
	    		if (list == null) {
	    			list = new ArrayList<KrnClass>();
	    			Database.classesByParentId.put(cls.parentId, list);
	    		}
    		}
    		if (!list.contains(cls))
    			list.add(cls);

		} else if ("attributeCache".equals(cacheName)) {
			KrnAttribute attr = (KrnAttribute)value;
			
			Database.attrsById.put(attr.id, attr);
			Database.attrsByUid.put(attr.uid, attr);
			
			Map<String, KrnAttribute> map = null;
			synchronized (Database.attrsByName) {
				map = Database.attrsByName.get(attr.classId);
				if (map == null) {
					map = Collections.synchronizedMap(new HashMap<String, KrnAttribute>());
					Database.attrsByName.put(attr.classId, map);
				}
			}
			map.put(attr.name, attr);
			
    		Set<KrnAttribute> set = null;
    		synchronized (Database.attrsByTypeId) {
	    		set = Database.attrsByTypeId.get(attr.typeClassId);
	    		if (set == null) {
	    			set = new HashSet<KrnAttribute>();
	    			Database.attrsByTypeId.put(attr.typeClassId, set);
	    		}
	    		if (!set.contains(attr))
	        		set.add(attr);
    		}
		} else if ("methodCache".equals(cacheName)) {
			KrnMethod m = (KrnMethod)value;
			Database.methodsByUid.put(m.uid, m);
			Map<String, KrnMethod> map = null;
			synchronized (Database.methodsByName) {
				map = Database.methodsByName.get(m.classId);
				if (map == null) {
					map = Collections.synchronizedMap(new HashMap<String, KrnMethod>());
					Database.methodsByName.put(m.classId, map);
				}
			}
			map.put(m.name, m);
		} else if ("protocolRulesCache".equals(cacheName)) {
			ProtocolRule rule = (ProtocolRule)value;
	    	synchronized (Database.rulesByEvent) {
	    		List<ProtocolRule> map = Database.rulesByEvent.get(rule.getEvent());
	    		if (map == null) {
	    			map = new ArrayList<ProtocolRule>();
	    			Database.rulesByEvent.put(rule.getEvent(), map);
	    		}
	    		if (!map.contains(rule))
	    			map.add(rule);
	    	}
	    	synchronized (Database.rulesByType) {
	    		List<ProtocolRule> map = Database.rulesByType.get(rule.getEventType());
	    		if (map == null) {
	    			map = new ArrayList<ProtocolRule>();
	    			Database.rulesByType.put(rule.getEventType(), map);
	    		}
	    		if (!map.contains(rule))
	    			map.add(rule);
	    	}
		} else if ("systemRightsCache".equals(cacheName)) {
			log.info("Добавление права доступа");

			SystemRight right = (SystemRight)value;
	    	synchronized (Database.rightByAction) {
	    		List<SystemRight> map = Database.rightByAction.get(right.getAction());
	    		if (map == null) {
	    			map = new ArrayList<SystemRight>();
	    			Database.rightByAction.put(right.getAction(), map);
	    		}
	    		if (!map.contains(right))
	    			map.add(right);
	    	}
		} else if ("userSessionCache".equals(cacheName)) {
			log.debug("Добавление сессии в кэш");
			ServerUserSession us = (ServerUserSession)value;
    		synchronized (Session.allUserSessionCache) {
    			Session.allUserSessionCache.put(us.getId(), us);
    		}
		} else if ("activeFlowCache".equals(cacheName)) {
			log.debug("Добавление потока в кэш");
			Long flowId = (Long)key;
			String activeFlow = (String)value;
			if (key != null && activeFlow != null) {
    			synchronized (ExecutionEngine.activeFlowCacheMap) {
	    			ExecutionEngine.activeFlowCacheMap.put(flowId, activeFlow);
				}
    		}
		}
	}

	@Override
	public void entryRemoved(String cacheName, Object key, Object oldValue) {
		if ("classCache".equals(cacheName)) {
			KrnClass cls = (KrnClass)oldValue;
			Database.classesByName.remove(cls.name);
			Database.classesById.remove(cls.id);
			Database.classesByUid.remove(cls.uid);
        	List<KrnClass> list = Database.classesByParentId.get(cls.parentId);
        	if (list != null) {
        		list.remove(cls);
        	}
		} else if ("attributeCache".equals(cacheName)) {
			KrnAttribute attr = (KrnAttribute)oldValue;
			
			Database.attrsById.remove(attr.id);
			Database.attrsByUid.remove(attr.uid);

			Map<String, KrnAttribute> map = Database.attrsByName.get(attr.classId);
			if (map != null) {
				map.remove(attr.name);
			}
			
        	Set<KrnAttribute> set = Database.attrsByTypeId.get(attr.typeClassId);
        	if (set != null) {
        		set.remove(attr);
        	}
		} else if ("methodCache".equals(cacheName)) {
			KrnMethod m = (KrnMethod)oldValue;
			Database.methodsByUid.remove(m.uid);
			Map<String, KrnMethod> map = Database.methodsByName.get(m.classId);
			if (map != null) {
				map.remove(m.name);
			}
			Driver2.removeMethodExpression(dsName, m.uid);
		} else if ("protocolRulesCache".equals(cacheName)) {
			ProtocolRule rule = (ProtocolRule)oldValue;
	    	synchronized(Database.rulesByEvent) {
	    		List<ProtocolRule> map = Database.rulesByEvent.get(rule.getEvent());
	        	if (map != null) {
	        		map.remove(rule);
	        	}
	    	}
	    	synchronized(Database.rulesByType) {
	    		List<ProtocolRule> map = Database.rulesByType.get(rule.getEventType());
	        	if (map != null) {
	        		map.remove(rule);
	        	}
	    	}
		} else if ("systemRightsCache".equals(cacheName)) {
			log.info("Удаление права доступа");
			SystemRight right = (SystemRight)oldValue;
	    	synchronized(Database.rightByAction) {
	    		List<SystemRight> map = Database.rightByAction.get(right.getAction());
	        	if (map != null) {
	        		map.remove(right);
	        	}
	    	}
		} else if ("userSessionCache".equals(cacheName)) {
			log.debug("Удаление сессии из кэша");
			UUID uuid = (UUID)key;
    		synchronized (Session.allUserSessionCache) {
    			Session.allUserSessionCache.remove(uuid);
    		}
		} else if ("activeFlowCache".equals(cacheName)) {
			log.debug("Удаление потока из кэша");
			Long flowId = (Long)key;
			if (key != null) {
    			synchronized (ExecutionEngine.activeFlowCacheMap) {
	    			ExecutionEngine.activeFlowCacheMap.remove(flowId);
				}
    		}
		}
	}

	@Override
	public void entryEvicted(String cacheName, Object key, Object oldValue) {
	}
}
