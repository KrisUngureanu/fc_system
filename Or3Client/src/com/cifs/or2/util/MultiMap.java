package com.cifs.or2.util;

import java.util.*;

public class MultiMap<K, V> {
    private Map<K, List<V>> map_ = new TreeMap<K, List<V>>();
    private Class<? extends List> colClass_;

    public MultiMap() {
        colClass_ = ArrayList.class;
    }

    public MultiMap(Class<? extends List> collectionClass) {
        colClass_ = collectionClass;
    }

    public void put(K key, V val) {
        try {
        	List<V> children = map_.get(key);
            if (children == null) {
                children = colClass_.newInstance();
                map_.put(key, children);
            }
            children.add(val);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void put(K key, List<V> vals) {
        map_.put(key, vals);
    }

    public List<V> get(Object key) {
        return map_.get(key);
    }

    public List<V> remove(Object key) {
        return map_.remove(key);
    }

    public Set<K> keySet() {
        return map_.keySet();
    }

    public void clear() {
        map_.clear();
    }
}