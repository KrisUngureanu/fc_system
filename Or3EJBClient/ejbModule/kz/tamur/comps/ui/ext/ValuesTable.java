package kz.tamur.comps.ui.ext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p/>
 * This class allows you to create key-value table from which you can either request a key by value or a value by key. Both key and value should be unique and adding same key or value will replace existing one in the lists.
 */
public class ValuesTable<K, V> implements Serializable {
    private List<K> keys;
    private List<V> values;

    public ValuesTable() {
        super();
        keys = new ArrayList<K>();
        values = new ArrayList<V>();
    }

    /**
     * Keys list retrieval
     */

    public List<K> getKeys() {
        return new ArrayList<K>(keys);
    }

    /**
     * Values list retrieval
     */

    public List<V> getValues() {
        return new ArrayList<V>(values);
    }

    /**
     * Values add methods
     */

    public void addAll(Map<K, V> data) {
        putAll(data);
    }

    public void putAll(Map<K, V> data) {
        for (Map.Entry<K, V> entry : data.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void addAll(ValuesTable<K, V> data) {
        putAll(data);
    }

    public void putAll(ValuesTable<K, V> data) {
        for (int i = 0; i < data.size(); i++) {
            put(data.getKey(i), data.getValue(i));
        }
    }

    public void add(K key, V value) {
        put(key, value);
    }

    public void put(K key, V value) {
        put(keys.size(), key, value);
    }

    public void add(int index, K key, V value) {
        put(index, key, value);
    }

    public void put(int index, K key, V value) {
        // Searching for existing key
        int existingKeyIndex = -1;
        if (keys.contains(key)) {
            existingKeyIndex = indexOfKey(key);
        }

        // Searching for existing value
        int existingValueIndex = -1;
        if (values.contains(value)) {
            existingValueIndex = indexOfValue(value);
        }

        // Removing existing records
        if (existingKeyIndex != -1 || existingValueIndex != -1) {
            if (existingKeyIndex > existingValueIndex) {
                if (existingKeyIndex != -1) {
                    keys.remove(existingKeyIndex);
                    values.remove(existingKeyIndex);
                }
                if (existingValueIndex != -1) {
                    keys.remove(existingValueIndex);
                    values.remove(existingValueIndex);
                }
            } else {
                if (existingValueIndex != -1) {
                    keys.remove(existingValueIndex);
                    values.remove(existingValueIndex);
                }
                if (existingKeyIndex != -1) {
                    keys.remove(existingKeyIndex);
                    values.remove(existingKeyIndex);
                }
            }
        }

        // Changing insert index
        if (existingKeyIndex != -1 && existingKeyIndex < index && existingValueIndex != -1 && existingValueIndex < index) {
            index -= 2;
        } else if (existingKeyIndex != -1 && existingKeyIndex < index || existingValueIndex != -1 && existingValueIndex < index) {
            index--;
        }

        // Adding new key-value pair
        keys.add(index, key);
        values.add(index, value);
    }

    /**
     * Values removal methods
     */

    public void remove(K key) {
        removeByKey(key);
    }

    public void removeByKey(K key) {
        remove(indexOfKey(key));
    }

    public void removeByValue(V value) {
        remove(indexOfValue(value));
    }

    public void remove(int index) {
        if (index >= 0 && index < keys.size()) {
            keys.remove(index);
            values.remove(index);
        }
    }

    /**
     * Values retrieval by index methods
     */

    public V get(int index) {
        return getValue(index);
    }

    public V getValue(int index) {
        return values.get(index);
    }

    public K getKey(int index) {
        return keys.get(index);
    }

    /**
     * Value/key retrieval
     */

    public V get(K key) {
        return getValue(key);
    }

    public V getValue(K key) {
        int index = indexOfKey(key);
        return index != -1 ? values.get(index) : null;
    }

    public K getKey(V value) {
        int index = indexOfValue(value);
        return index != -1 ? keys.get(index) : null;
    }

    /**
     * Check for key or value existance
     */

    public boolean containsKey(K key) {
        return keys.contains(key);
    }

    public boolean containsValue(V value) {
        return values.contains(value);
    }

    /**
     * Index of keys and velues
     */

    public int indexOf(K key) {
        return indexOfKey(key);
    }

    public int indexOfKey(K key) {
        return keys.indexOf(key);
    }

    public int indexOfValue(V value) {
        return values.indexOf(value);
    }

    /**
     * Table size
     */

    public int size() {
        return keys.size();
    }
}