package com.cifs.or2.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MMap<K, V, C extends Collection<V>> {
	
	private Map<K, C> map = new HashMap<K, C>();
	private Class<? extends C> colClass;
	
	public MMap(Class<? extends C> colClass) {
		this.colClass = colClass;
	}
	
	public void put(K key, V value) {
		C set = map.get(key);
		if (set == null) {
			try {
				set = (C)colClass.newInstance();
				map.put(key, set);
			} catch (IllegalAccessException e) {
			} catch (InstantiationException e) {
			}
		}
		set.add(value);
	}
	
	public C get(K key) {
		return map.get(key);
	}
	
	public C remove(K key) {
		return map.remove(key);
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
}
