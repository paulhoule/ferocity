package com.ontology2.ferocity;

import java.util.*;
import java.util.function.Supplier;

public class DefaultMap<K,V> implements Map<K,V> {
    final Map<K,V> innerMap;
    final Supplier<V> defaultGenerator;

    public DefaultMap(Map<K, V> innerMap, Supplier<V> defaultGenerator) {
        this.innerMap = innerMap;
        this.defaultGenerator = defaultGenerator;
    }

    @Override
    public int size() {
        return innerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return innerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return innerMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if(!innerMap.containsKey(key)) {
            innerMap.put((K) key, defaultGenerator.get());
        }
        return innerMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return innerMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return innerMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        innerMap.putAll(m);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return innerMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return innerMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return innerMap.entrySet();
    }

    public static <K,V> DefaultMap<K, List<V>> newListMultiMap() {
        return new DefaultMap(
            new HashMap<K, List<V>>(), () -> new ArrayList<V>()
        );
    }
}
