package kz.tamur.or3ee.server.kit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

import kz.tamur.or3ee.common.UserSession;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.RMICachePeer_Stub;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

public class Cache<K, V> {
	
	private final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Cache.class.getName());
	
	// used when ehcache
	private final net.sf.ehcache.Cache ehcache;
	// used when hazelcast
	private final IMap<K, V> hzMap;
	private final ReplicatedMap<K, V> hzrMap;
	private final HazelcastInstance hzInstance;
	// used when no cluster (simple map)
	private final Map<K, V> map;
	private final String cacheName;
	private CacheListener<? super K, ? super V> listener;
	
	public Cache(IMap<K, V> hzMap, HazelcastInstance hzInstance) {
		this.ehcache = null;
		this.hzMap = hzMap;
		this.hzrMap = null;
		this.hzInstance = hzInstance;
		this.map = null;
		this.cacheName = hzMap.getName();
	}

	public Cache(ReplicatedMap<K, V> hzrMap, HazelcastInstance hzInstance) {
		this.ehcache = null;
		this.hzMap = null;
		this.hzrMap = hzrMap;
		this.hzInstance = hzInstance;
		this.map = null;
		this.cacheName = hzrMap.getName();
	}

	public Cache(net.sf.ehcache.Cache ehcache) {
		this.ehcache = ehcache;
		this.hzMap = null;
		this.hzrMap = null;
		this.hzInstance = null;
		this.map = null;
		this.cacheName = ehcache.getName();
	}
	
	public Cache(String name) {
		this.ehcache = null;
		this.hzMap = null;
		this.hzrMap = null;
		this.hzInstance = null;
		this.map = new HashMap<>();
		this.cacheName = name;
	}

	public String getName() {
		return cacheName;
	}

	public V get(K key) {
		if (hzMap != null)
			return hzMap.get(key);
		else if (hzrMap != null)
			return hzrMap.get(key);
		else if (ehcache != null) {
			Element e = ehcache.get(key);
			if (e != null)
				return (V) e.getObjectValue();
		} else
			return map.get(key);
		
		return null;
	}

	public void put(K key, V value) {
		if (hzMap != null)
			hzMap.put(key, value);
		else if (hzrMap != null)
			hzrMap.put(key, value);
		else if (ehcache != null) {
			ehcache.put(new Element(key, value));
		} else {
			V oldValue = map.put(key, value);
			if (listener != null) {
				if (oldValue == null)
					listener.entryAdded(cacheName, key, value, oldValue);
				else
					listener.entryUpdated(cacheName, key, value, oldValue);
				
				// удаляем ServerMessage сразу после обработки
				if ("ServerMessageCache".equals(cacheName))
					remove(key);
			}
		}
	}

	public Collection<K> getKeys() {
		if (hzMap != null)
			return hzMap.keySet();
		else if (hzrMap != null)
			return hzrMap.keySet();
		else if (ehcache != null)
			return ehcache.getKeys();
		else
			return map.keySet();
	}

	public int getSize() {
		if (hzMap != null)
			return hzMap.size();
		else if (hzrMap != null)
			return hzrMap.size();
		else if (ehcache != null)
			return ehcache.getSize();
		else
			return map.size();
	}

	public void remove(K key) {
		if (hzMap != null)
			hzMap.remove(key);
		else if (hzrMap != null)
			hzrMap.remove(key);
		else if (ehcache != null)
			ehcache.remove(key);
		else {
			V oldValue = map.remove(key);
			if (oldValue != null && listener != null)
				listener.entryRemoved(cacheName, key, oldValue);
		}
	}
	
	public V find(String attr, String value) {
		if (hzMap != null) {
			EntryObject e = new PredicateBuilder().getEntryObject();
			Predicate predicate = e.get(attr).equal(value);
			Collection<V> res = hzMap.values(predicate);
			if (res.size() > 0)
				return res.iterator().next();
		} else if (hzrMap != null) {
			if (hzrMap.values().size() > 0) {
				V v = hzrMap.values().iterator().next();
				try {
					Field field = v.getClass().getField(attr);
		            if (field != null) {
		            	for (V val : hzrMap.values()) {
		            		if (value.equals(field.get(val)))
		            			return val;
		            	}
		            }
				} catch (Throwable e) {
					log.error(e, e);
				}
			}
		} else if (ehcache != null) {
	    	Attribute<String> nameAttr = ehcache.getSearchAttribute(attr);
	    	Results res = ehcache.createQuery().includeValues().addCriteria(nameAttr.eq(value)).execute();
	    	List<Result> r = res.range(0, 1);
	    	if (r.size() > 0)
	    		return (V) r.get(0).getValue();
		} else {
			if (map.values().size() > 0) {
				V v = map.values().iterator().next();
				try {
					Field field = v.getClass().getField(attr);
		            if (field != null) {
		            	for (V val : map.values()) {
		            		if (value.equals(field.get(val)))
		            			return val;
		            	}
		            }
				} catch (Throwable e) {
					log.error(e, e);
				}
			}
		}
    	return null;
	}
	
	public List<V> findAll(String attr, String value) {
		List<V> all = new ArrayList<>();
		if (hzMap != null) {
			EntryObject e = new PredicateBuilder().getEntryObject();
			Predicate predicate = e.get(attr).equal(value);
			Collection<V> res = hzMap.values(predicate);
			for (Iterator<V> it = res.iterator(); it.hasNext();)
				all.add(it.next());
		} else if (hzrMap != null) {
			if (hzrMap.values().size() > 0) {
				V v = map.values().iterator().next();
				try {
					Field field = v.getClass().getField(attr);
		            if (field != null) {
		            	for (V val : hzrMap.values()) {
		            		if (value.equals(field.get(val)))
		            			all.add(val);
		            	}
		            }
				} catch (Throwable e) {
					log.error(e, e);
				}
			}
		} else if (ehcache != null) {
	    	Attribute<String> nameAttr = ehcache.getSearchAttribute(attr);
	    	Results res = ehcache.createQuery().includeValues().addCriteria(nameAttr.eq(value)).execute();
	    	List<Result> r = res.all();

	    	for (Iterator<Result> it = r.iterator(); it.hasNext();)
				all.add((V)it.next().getValue());
		} else {
			if (map.values().size() > 0) {
				V v = map.values().iterator().next();
				try {
					Field field = v.getClass().getField(attr);
		            if (field != null) {
		            	for (V val : map.values()) {
		            		if (value.equals(field.get(val)))
		            			all.add(val);
		            	}
		            }
				} catch (Throwable e) {
					log.error(e, e);
				}
			}
		}
    	return all;
	}
	
	public void printClusterInfo() {
		log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (ehcache != null) {
			try {
				List connectedPeers = ehcache.getCacheManager().getCacheManagerPeerProvider("RMI").listRemoteCachePeers(ehcache);
				if (connectedPeers.size() > 0) {
					for (int i=0; i<connectedPeers.size(); i++) {
						Object peer = connectedPeers.get(i);
						log.warn("ALREADY CONNECTED PEAR = " + ((peer instanceof RMICachePeer_Stub) ? ((RMICachePeer_Stub)peer).getUrl() : peer));
					}
				} else {
					log.info("NO CONNECTED PEERS!!! OK!!!");
				}
			} catch (Exception e) {
				log.error("NOT RMI");
			}
		} else if (hzInstance != null) {
			Set<Member> members = hzInstance.getCluster().getMembers();
			for (Member member : members) {
				log.warn("ALREADY CONNECTED PEAR = " + member.getAddress().getHost() + ":" + member.getAddress().getPort());
			}
		} else {
			log.info("RUNNING STANDALONE - WITHOUT CLUSTER");
		}
		log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	public void addEntryListener(final CacheListener<? super K, ? super V> listener) {
		if (hzMap != null) {
			MapListener l = new HazelcastEventListener<K, V>(listener);
			hzMap.addEntryListener(l, true);
		} else if (hzrMap != null) {
			EntryListener<K, V> l = new HazelcastEventListener<K, V>(listener);
			hzrMap.addEntryListener(l);
		} else if (ehcache != null) {
			CacheEventListener l = new EhcacheEventListener(listener);
			ehcache.getCacheEventNotificationService().registerListener(l, NotificationScope.ALL);
		} else {
			this.listener = listener;
		}
	}
	
	private class HazelcastEventListener<K, V> implements EntryListener<K, V> {
		private final CacheListener<? super K, ? super V> listener;
		
		public HazelcastEventListener(CacheListener<? super K, ? super V> listener) {
			this.listener = listener;
		}

		@Override
		public void entryAdded(EntryEvent<K, V> e) {
			listener.entryAdded(e.getName(), e.getKey(), e.getValue(), e.getOldValue());
		}

		@Override
		public void entryUpdated(EntryEvent<K, V> e) {
			listener.entryUpdated(e.getName(), e.getKey(), e.getValue(), e.getOldValue());
		}

		@Override
		public void entryRemoved(EntryEvent<K, V> e) {
			listener.entryRemoved(e.getName(), e.getKey(), e.getOldValue());
		}

		@Override
		public void entryEvicted(EntryEvent<K, V> e) {
			listener.entryEvicted(e.getName(), e.getKey(), e.getOldValue());
		}

		@Override
		public void mapCleared(MapEvent event) {}

		@Override
		public void mapEvicted(MapEvent event) {}
	}
	
	private class EhcacheEventListener implements CacheEventListener {
		private final CacheListener<? super K, ? super V> listener;
		
		public EhcacheEventListener(CacheListener<? super K, ? super V> listener) {
			this.listener = listener;
		}

		@Override
		public void notifyElementPut(Ehcache cache, Element e) throws CacheException {
			listener.entryAdded(cache.getName(), (K)e.getObjectKey(), (V)e.getObjectValue(), null);
		}

		@Override
		public void notifyElementUpdated(Ehcache cache, Element e) throws CacheException {
			listener.entryUpdated(cache.getName(), (K)e.getObjectKey(), (V)e.getObjectValue(), null);
		}
		
		@Override
		public void notifyElementRemoved(Ehcache cache, Element e) throws CacheException {
			listener.entryRemoved(cache.getName(), (K)e.getObjectKey(), (V)e.getObjectValue());
		}
		
		@Override
		public void notifyElementExpired(Ehcache cache, Element e) {
			listener.entryEvicted(cache.getName(), (K)e.getObjectKey(), (V)e.getObjectValue());
		}
		
		@Override
		public void notifyElementEvicted(Ehcache cache, Element e) {
			listener.entryEvicted(cache.getName(), (K)e.getObjectKey(), (V)e.getObjectValue());
		}
		
		@Override
		public void notifyRemoveAll(Ehcache cache) {}

		@Override
		public void dispose() {}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return new EhcacheEventListener(listener);
		}
	}
}
