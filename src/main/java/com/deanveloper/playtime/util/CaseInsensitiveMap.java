package com.deanveloper.playtime.util;

import java.util.HashMap;

/**
 * @author Dean
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {
    @Override
    public V put(String key, V value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            return super.get(((String) key).toLowerCase());
        } else {
            throw new IllegalArgumentException("Key must be of type String!");
        }
    }
}