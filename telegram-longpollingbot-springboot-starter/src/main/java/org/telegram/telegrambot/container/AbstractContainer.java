package org.telegram.telegrambot.container;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractContainer<K, V> {

    protected final Map<K, V> container = new ConcurrentHashMap<>();

    public Optional<V> get(K key) {
        V value = container.get(key);
        return Optional.ofNullable(value);
    }

    public void put(K key, V value) {
        container.put(key, value);
    }

    public Set<Map.Entry<K, V>> getEntrySet() {
        return container.entrySet();
    }

    public Set<K> getKeySet() {
        return container.keySet();
    }

    public Collection<V> getValues() {
        return container.values();
    }

    public boolean containsKey(K key) {
        return container.containsKey(key);
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return container.computeIfAbsent(key, mappingFunction);
    }
}
