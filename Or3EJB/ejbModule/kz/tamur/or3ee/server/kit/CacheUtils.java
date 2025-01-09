package kz.tamur.or3ee.server.kit;

import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.FactoryConfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.HazelcastInstance;

public final class CacheUtils {
	
	public static final int CACHE_NO = 0;
	public static final int CACHE_EHCACHE = 1;
	public static final int CACHE_HAZELCAST = 2;
	
	private static CacheManager mgr;
	private static int cacheType = -1;
	
	private static final Pattern URL_PATTERN = Pattern.compile("\\[(.+?)(-(\\d+)|(,(.*?))*)\\]");
	private static final Pattern HOST_PATTERN = Pattern.compile("//(.+?):(/d+)");
    
	private static final Log LOG = LogFactory.getLog((UserSession.SERVER_ID != null
    		? (UserSession.SERVER_ID + ".") : "") + CacheUtils.class.getName());
	
	public static <K, V> kz.tamur.or3ee.server.kit.Cache<K,V> getCache(String name) {
		int cacheType = getCacheType();
		
		if (cacheType == CACHE_HAZELCAST) {
			HazelcastInstance hz = HazelcastCacheUtils.getCacheManager();
			if ("userSessionCache".equals(name) ||
					"activeFlowCache".equals(name) ||
					"systemRightsCache".equals(name))
				return new kz.tamur.or3ee.server.kit.Cache<K,V>(HazelcastCacheUtils.<K,V>getReplicatedMap(name), hz);
			else
				return new kz.tamur.or3ee.server.kit.Cache<K,V>(HazelcastCacheUtils.<K,V>getCache(name), hz);
		} else if (cacheType == CACHE_EHCACHE) {
			CacheManager mgr = getCacheManager();
			return new kz.tamur.or3ee.server.kit.Cache<K,V>(mgr.getCache(name));
		} else {
			return new kz.tamur.or3ee.server.kit.Cache<K,V>(name);
		}
	}
	
	private static synchronized int getCacheType() {
		if (cacheType == -1) {
			cacheType = CACHE_NO;

			String confUrl = Funcs.getSystemProperty("cache.confUrl");
			
			if (confUrl != null) {
				if (Funcs.isValid(confUrl)) {
					if (HazelcastCacheUtils.isHazelcastConf(confUrl))
						cacheType = CACHE_HAZELCAST;
					else if (CacheUtils.isEhcacheConf(confUrl)) {
						cacheType = CACHE_EHCACHE;
					}
				}
			}
		}
		return cacheType;
	}
	
	private static boolean isEhcacheConf(String confUrl) {
		try {
			ConfigurationFactory.parseConfiguration(new URL(confUrl));
			return true;
		} catch (Throwable e) {
			LOG.warn("Not Ehcache");
		}
		return false;
	}
	
	private static synchronized CacheManager getCacheManager() {
		if (mgr == null) {
			try {
				String confUrl = Funcs.getSystemProperty("cache.confUrl");
				String myPort = System.getProperty("cache.listenerPort");
		
				if (Funcs.isValid(confUrl)) {
					Configuration conf = ConfigurationFactory.parseConfiguration(new URL(confUrl));
					List<FactoryConfiguration> factoryConfs = conf.getCacheManagerPeerProviderFactoryConfiguration();
					if (factoryConfs.size() > 0) {
						FactoryConfiguration peerProviderConf = factoryConfs.get(0);
						String peerProps = peerProviderConf.getProperties();
						if (peerProps.indexOf("peerDiscovery=manual") >= 0) {
							FactoryConfiguration peerListenerConf =
									conf.getCacheManagerPeerListenerFactoryConfigurations().get(0);
							String props = peerListenerConf.getProperties();
							String propsSeparator = peerListenerConf.getPropertySeparator();
							int port = myPort != null ? Integer.parseInt(myPort) : 0;
							if (port > 0) {
								if (props.indexOf("port=") == -1) {
									StringBuilder newProps = new StringBuilder();
									newProps.append(props);
									if (props.length() > 0) {
										newProps.append(peerListenerConf.getPropertySeparator());
									}
									newProps.append("port=").append(port);
									peerListenerConf.setProperties(newProps.toString());
								}
							} else {
								int start = props.indexOf("port=");
								int end = props.indexOf(propsSeparator, start + 5);
								end = (end > -1) ? end : props.length();
								
								port = Integer.parseInt(props.substring(start + 5, end));
							}
							int start = props.indexOf("hostName=");
							String myHost = start != -1 ? props.substring(start + 9, props.indexOf(propsSeparator, start + 9)) : InetAddress.getLocalHost().getHostName();
		
							String newPeerProps = expandUrls(peerProps, peerProviderConf.getPropertySeparator(), myHost + ":" + port);
							LOG.info("Starting ehcache with peerProps = " + newPeerProps);
							LOG.info("peerListenerProps = " + peerListenerConf.getProperties());
							LOG.info("myHost:myPort = " + myHost + ":" + port);
							peerProviderConf.setProperties(newPeerProps);
						} else {
							LOG.info("Starting ehcache with automatic peerProps = " + peerProps);
						}
					}
					mgr = CacheManager.newInstance(conf);
				}
			} catch (Exception e) {
				LOG.error("Ошибка при инициализации кэша.", e);
			}
		}
		return mgr;
	}
	
	public static void main(String[] args) throws Exception {
		String serverId = "03";
		
/*		String confUrl = "file:/D:/tmp/kyzmet/ehcache." + serverId + ".xml";
		CacheManager mgr = getCacheManager(confUrl, 40000 + Integer.parseInt(serverId));
		Cache cs = mgr.getCache("testCache1");

		CacheListener cl = new CacheListener(serverId, cs);
		cs.getCacheEventNotificationService().registerListener(cl, NotificationScope.ALL);

		System.out.println(String.format("cache size: %s", cs.getSize()));
		for (Object key : cs.getKeys()) {
			cl.notifyElementPut(cs, cs.get(key));
		}
		
		cs.put(new Element(UUID.randomUUID().toString(), "Message from cache " + serverId));
*/

/*		for (int i=4; i < 8; i++) {
			String confUrl = "file:/D:/tmp/kyzmet/ehcache.2.xml";
			CacheManager mgr1 = getCacheManager(confUrl, 49996 + i);
			cs[i] = mgr1.getCache("testCache1");
			cs[i].getCacheEventNotificationService().registerListener(new CacheListener("0" + i, cs[i]), NotificationScope.ALL);
		}
		
		cs[4].put(new Element("message", "Message from cache 4"));
*/
/*		Cache[] cs1 = new Cache[12];
		Cache[] cs2 = new Cache[12];
		
		for (int i=0; i < 4; i++) {
			String confUrl = "file:/D:/tmp/kyzmet/ehcache.11.xml";
			CacheManager mgr1 = getCacheManager(confUrl, 40000 + i);
			cs1[i] = mgr1.getCache("testCache1");
			cs1[i].getCacheEventNotificationService().registerListener(new CacheListener("0" + i, cs1[i]), NotificationScope.ALL);
			cs1[4+i] = mgr1.getCache("testCache2");
			cs1[4+i].getCacheEventNotificationService().registerListener(new CacheListener("0" + i, cs1[4+i]), NotificationScope.ALL);
			cs1[8+i] = mgr1.getCache("testCache3");
			cs1[8+i].getCacheEventNotificationService().registerListener(new CacheListener("0" + i, cs1[8+i]), NotificationScope.ALL);
		}
		for (int i=0; i < 4; i++) {
			String confUrl = "file:/D:/tmp/kyzmet/ehcache.12.xml";
			CacheManager mgr1 = getCacheManager(confUrl, 40004 + i);
			cs2[i] = mgr1.getCache("testCache1");
			cs2[i].getCacheEventNotificationService().registerListener(new CacheListener("0" + (4+i), cs2[i]), NotificationScope.ALL);
			cs2[4+i] = mgr1.getCache("testCache2");
			cs2[4+i].getCacheEventNotificationService().registerListener(new CacheListener("0" + (4+i), cs2[4+i]), NotificationScope.ALL);
			cs2[8+i] = mgr1.getCache("testCache3");
			cs2[8+i].getCacheEventNotificationService().registerListener(new CacheListener("0" + (4+i), cs2[8+i]), NotificationScope.ALL);
		}
		
		cs1[0].put(new Element("message", "Message from cache 11"));
		cs1[4].put(new Element("message", "Message from cache 12"));
		cs1[8].put(new Element("message", "Message from cache 13"));
*///		cs[4].put(new Element("message", "Message from cache 4"));
	}
	
/*	private static class CacheListener implements CacheEventListener {
		
		String serverId;
		Cache cache;

		public CacheListener(String serverId, Cache cache) {
			this.serverId = serverId;
			this.cache = cache;
		}

		@Override
		public void notifyElementPut(Ehcache cache, Element e) throws CacheException {
			System.out.println(String.format("notifyElementPut: server-%s, cache: %s, key: %s, message: %s", serverId, cache.getName(), e.getObjectKey(), e.getObjectValue()));
			//cache.remove(e.getObjectKey());
			
		}

		@Override
		public void notifyElementRemoved(Ehcache cache, Element e) throws CacheException {
			System.out.println(String.format("notifyElementRemoved: server-%s, cache: %s, key: %s, message: %s", serverId, cache.getName(), e.getObjectKey(), e.getObjectValue()));
		}

		@Override
		public void notifyElementUpdated(Ehcache cache, Element e) throws CacheException {
			System.out.println(String.format("notifyElementUpdated: server-%s, cache: %s, key: %s, message: %s", serverId, cache.getName(), e.getObjectKey(), e.getObjectValue()));
		}

		@Override
		public void notifyElementExpired(Ehcache cache, Element e) {
			System.out.println(String.format("notifyElementExpired: server-%s, cache: %s, key: %s, message: %s", serverId, cache.getName(), e.getObjectKey(), e.getObjectValue()));
		}

		@Override
		public void notifyElementEvicted(Ehcache cache, Element e) {
			System.out.println(String.format("notifyElementEvicted: server-%s, cache: %s, key: %s, message: %s", serverId, cache.getName(), e.getObjectKey(), e.getObjectValue()));
		}

		@Override
		public void notifyRemoveAll(Ehcache cache) {
			System.out.println(String.format("notifyRemoveAll: server-%s, cache: %s", serverId, cache.getName()));
		}

		@Override
		public void dispose() {
			System.out.println(String.format("dispose: server-%s", serverId));
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			return new CacheListener(serverId, cache);
		}
	}
*/	
	private static String expandUrls(String properties, String propertySeparator, String myPrefix) {
		int start = properties.indexOf("rmiUrls=");
		if (start != -1) {
			start += 8;
			StringBuilder res = new StringBuilder();
			res.append(properties.subSequence(0, start));
			final int end = properties.indexOf(propertySeparator, start);
			final String urlsStr = (end == -1) ? properties.substring(start) : properties.substring(start, end);
			String[] urls = urlsStr.split("\\|");
			for (int i = 0; i < urls.length; i++) {
				char ch = res.charAt(res.length() - 1);
				if (ch != '|' && ch != '=') {
					res.append('|');
				}
				StringBuilder expandedUrl = new StringBuilder();
				expandUrl(urls[i], 0, "", myPrefix, expandedUrl);
				res.append(expandedUrl.toString());
			}
			if (res.charAt(res.length() - 1) == '|') res.deleteCharAt(res.length() - 1);
			return res.toString();
		} else {
			return properties;
		}
	}
	
	private static void expandUrl(String url, int pos, String prefix, String myPrefix, StringBuilder res) {
		Matcher m = URL_PATTERN.matcher(url);
		if (m.find(pos)) {
			final String str = m.group(2);
			final char startChar = str.charAt(0);
			if (startChar == ',') {
				StringBuilder newPrefix = new StringBuilder();
				newPrefix.append(prefix);
				newPrefix.append(url.substring(pos, m.start()));
				newPrefix.append(m.group(1));
				expandUrl(url, m.end(), newPrefix.toString(), myPrefix, res);
				for (int start = 0; start != -1;) {
					start++;
					int end = str.indexOf(',', start);
					newPrefix = new StringBuilder();
					newPrefix.append(prefix);
					newPrefix.append(url.substring(pos, m.start()));
					newPrefix.append(end == -1 ? str.substring(start) : str.substring(start, end));
					expandUrl(url, m.end(), newPrefix.toString(), myPrefix, res);
					start = end;
				}
			} else if (startChar == '-') {
				int start = Integer.parseInt(m.group(1));
				int end = Integer.parseInt(m.group(3));
				for (int i = start; i <= end; i++) {
					StringBuilder newPrefix = new StringBuilder();
					newPrefix.append(prefix);
					newPrefix.append(url.substring(pos, m.start()));
					newPrefix.append(i);
					expandUrl(url, m.end(), newPrefix.toString(), myPrefix, res);
				}
			}
		} else {
			String expUrl = prefix + url.substring(pos);
			if (expUrl.indexOf(myPrefix) == -1) {
				if (res.length() > 0) {
					res.append('|');
				}
				res.append(prefix);
				res.append(url.substring(pos));
			}
		}
	}
}
