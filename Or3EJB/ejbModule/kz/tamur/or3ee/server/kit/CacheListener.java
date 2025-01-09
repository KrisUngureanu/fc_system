package kz.tamur.or3ee.server.kit;

public interface CacheListener<K, V> {

	void entryAdded(String cacheName, K key, V value, V oldValue);
	void entryUpdated(String cacheName, K key, V value, V oldValue);
	void entryRemoved(String cacheName, K key, V oldValue);
	void entryEvicted(String cacheName, K key, V oldValue);

}
