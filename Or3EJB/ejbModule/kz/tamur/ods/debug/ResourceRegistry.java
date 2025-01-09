package kz.tamur.ods.debug;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResourceRegistry {
	
	private static boolean enabled = Boolean.parseBoolean(System.getProperty("resourceRegistry.enabled", "true"));
	
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ResourceRegistry.class.getName());
	
	private static ResourceRegistry inst;
	
	private Map<Object, Item> reg = Collections.synchronizedMap(new IdentityHashMap<Object, Item>());
	
	private ResourceRegistry() {
	}
	
	public static synchronized ResourceRegistry instance() {
		if (inst == null){
			inst = new ResourceRegistry();
			if(!enabled)
					log.info("Resource logging is administratively disabled.");
		}
		return inst;
	}
	
	public void resourceAllocated(Object resource) {
		if (enabled)
			reg.put(resource, new Item(System.currentTimeMillis()));
	}

	public void resourceReleased(Object resource) {
		if (enabled)
			reg.remove(resource);
	}
	
	public void dump(long period) {
		if (enabled) {
			long time = System.currentTimeMillis();
			synchronized (reg) {
				log.info("BEGIN RESOURCE DUMP =============");
				for (Object resource : reg.keySet()) {
					Item item = reg.get(resource);
					long dtime = time - item.time;
					if (dtime >= period) {
						log.warn("Resource: " + resource.toString() + " Time: " + dtime);
						log.warn("Thread: " + item.thread.getName());
						log.warn("Stacktrace:");
						StackTraceElement[] stackTrace = item.thread.getStackTrace();
						for (int i = 2; i < stackTrace.length; i++) {
							log.warn('\t' + stackTrace[i].toString());
						}
						log.warn("");
					}
				}
				log.info("END RESOURCE DUMP ===============");
			}
		}
	}
	
	public void setEnabled(boolean enabled) {
		ResourceRegistry.enabled = enabled;
		if (!enabled)
			reg.clear();
	}
	
	private static final class Item {
		public final long time;
		public final Thread thread;
		
		public Item(long time) {
			super();
			this.time = time;
			this.thread = Thread.currentThread();
		}
	}
}
