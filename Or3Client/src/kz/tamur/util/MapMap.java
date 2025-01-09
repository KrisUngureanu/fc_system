package kz.tamur.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MapMap<K1, K2, V> {
    private Map<K1, Map<K2, V>> map1;

    public MapMap() {
        map1 = new TreeMap<K1, Map<K2, V>>();
    }

    public void put(K1 key1, K2 key2, V val) {
        try {
        	Map<K2, V> map2 = map1.get(key1);
            if (map2 == null) {
                map2 = new TreeMap<K2, V>();
                map1.put(key1, map2);
            }
            map2.put(key2, val);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void put(K1 key, Map<K2, V> values) {
        map1.put(key, values);
    }

    public Map<K2, V> get(K1 key1) {
        return map1.get(key1);
    }

    public V get(K1 key1, K2 key2) {
    	Map<K2, V> map2 = map1.get(key1);
        if (map2 == null) {
            return null;
        }
        return map2.get(key2);
    }

    public Map<K2, V> remove(K1 key1) {
        return map1.remove(key1);
    }

    public V remove(K1 key1, K2 key2) {
    	Map<K2, V> map2 = map1.get(key1);
        if (map2 != null) {
            return map2.remove(key2);
        } else {
            return null;
        }
    }

    public Set<K1> keySet() {
        return map1.keySet();
    }

    public void clear() {
        map1.clear();
    }
}