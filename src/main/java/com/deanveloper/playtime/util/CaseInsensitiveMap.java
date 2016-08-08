package com.deanveloper.playtime.util;

import java.util.HashMap;

/**
 * Created by Dean on 8/7/2016.
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {
    @Override
    public V put(String key, V value) {
        return super.put(key.toLowerCase(), value);
    }

    // not @Override because that would require the key parameter to be of type Object
    public V get(String key) {
        return super.get(key.toLowerCase());
    }
}