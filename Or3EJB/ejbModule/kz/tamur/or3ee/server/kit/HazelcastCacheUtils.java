package kz.tamur.or3ee.server.kit;

import java.net.URL;
import java.util.Iterator;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.map.listener.MapListener;

public final class HazelcastCacheUtils {
	
	private static HazelcastInstance mgr;
	
	private static final Log LOG = LogFactory.getLog((UserSession.SERVER_ID != null
    		? (UserSession.SERVER_ID + ".") : "") + HazelcastCacheUtils.class.getName());
	
	private HazelcastCacheUtils() {
	}

	public static <K, V> IMap<K, V> getCache(String name) {
		if (mgr == null) {
			mgr = getCacheManager();
		}
		return mgr.<K, V> getMap(name);
	}

	public static <K, V> ReplicatedMap<K, V> getReplicatedMap(String name) {
		if (mgr == null) {
			mgr = getCacheManager();
		}
		return mgr.<K, V> getReplicatedMap(name);
	}

	protected static synchronized HazelcastInstance getCacheManager() {
		if (mgr == null) {
			try {
				String confUrl = Funcs.getSystemProperty("cache.confUrl");
				String myPort = System.getProperty("cache.listenerPort");

				if (Funcs.isValid(confUrl)) {
					Config cfg = new XmlConfigBuilder(new URL(confUrl)).build();
					int port = myPort != null ? Integer.parseInt(myPort) : 0;
					if (port > 0) {
						cfg.getNetworkConfig().setPort(port);
					}
					mgr = Hazelcast.newHazelcastInstance(cfg);
				}
			} catch (Exception e) {
				LOG.error("Ошибка при инициализации кэша.", e);
			}
		}
		return mgr;
	}
	
	public static boolean isHazelcastConf(String confUrl) {
		try {
			new XmlConfigBuilder(new URL(confUrl)).build();
			return true;
		} catch (Throwable e) {
			LOG.warn("Not Hazelcast");
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		String serverId = "01";
		
/*		Config config = new Config();
		NetworkConfig network = config.getNetworkConfig();
		network.setPort(6000 + Integer.parseInt(serverId)).setPortCount(20);
		network.setPortAutoIncrement(true);
		JoinConfig join = network.getJoin();
		join.getMulticastConfig().setEnabled(false);
		join.getTcpIpConfig()
		  	.addMember("192.168.13.107:6001")
		  	.addMember("192.168.13.107:6002")
		  	.addMember("192.168.13.107:6003")
		  	.addMember("192.168.13.107:6004")
		  	.addMember("192.168.13.107:6005")
		  	.setEnabled(true);
*/		
		System.setProperty("cache.confUrl", "file:/D:/work/or3ee/hazelcast.xml");
		System.setProperty("cache.listenerPort", 60 + serverId);
		
		IMap<String, String> c = HazelcastCacheUtils.<String, String> getCache("sessions");
		
		MapListener listener = new HazelcastMapListener();
		c.addEntryListener(listener, true);
		
		for (Iterator<String> it = c.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			System.out.println(String.format("Key: %s, value: %s", key, c.get(key)));
		}
		
		c.put("1", "Message 1 from cache " + serverId);
		c.put("2", "Message 2 from cache " + serverId);
		c.put("1", "Message 1 from cache " + serverId);
		c.put("1", "Message 11 from cache " + serverId);
		
		c.remove("2");
	}
	
	private static class HazelcastMapListener implements EntryListener<String, String> {

		@Override
		public void entryAdded(EntryEvent<String, String> e) {
			System.out.println(String.format("entryAdded: %s, %s, %s, %s, %s, %s, %s, %s", 
					e.getEventType().name(), e.getKey(), e.getMember().getUuid(),
					e.getMergingValue(), e.getName(), e.getValue(), e.getOldValue(), e.getSource()));
		}

		@Override
		public void entryRemoved(EntryEvent<String, String> e) {
			System.out.println(String.format("entryRemoved: %s, %s, %s, %s, %s, %s, %s, %s", 
					e.getEventType().name(), e.getKey(), e.getMember().getUuid(),
					e.getMergingValue(), e.getName(), e.getValue(), e.getOldValue(), e.getSource()));
		}

		@Override
		public void entryEvicted(EntryEvent<String, String> e) {
			System.out.println(String.format("entryEvicted: %s, %s, %s, %s, %s, %s, %s, %s", 
					e.getEventType().name(), e.getKey(), e.getMember().getUuid(),
					e.getMergingValue(), e.getName(), e.getValue(), e.getOldValue(), e.getSource()));
		}

		@Override
		public void mapEvicted(MapEvent event) {
		}

		@Override
		public void mapCleared(MapEvent event) {
		}

		@Override
		public void entryUpdated(EntryEvent<String, String> e) {
			System.out.println(String.format("entryUpdated: %s, %s, %s, %s, %s, %s, %s, %s", 
					e.getEventType().name(), e.getKey(), e.getMember().getUuid(),
					e.getMergingValue(), e.getName(), e.getValue(), e.getOldValue(), e.getSource()));
		}
	}
}
