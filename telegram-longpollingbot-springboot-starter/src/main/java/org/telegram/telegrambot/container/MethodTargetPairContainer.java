package org.telegram.telegrambot.container;

import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MethodTargetPairContainer<T> {

    protected final Map<T, MethodTargetPair> methodTargetPairs = new ConcurrentHashMap<>();

    public Optional<MethodTargetPair> getMethodTargetPair(T key) {
        MethodTargetPair methodTargetPair = methodTargetPairs.get(key);
        return Optional.ofNullable(methodTargetPair);
    }

    public void putMethodTargetPair(T key, MethodTargetPair methodTargetPair) {
        methodTargetPairs.put(key, methodTargetPair);
    }

    public Set<Map.Entry<T, MethodTargetPair>> getEntrySet() {
        return methodTargetPairs.entrySet();
    }
}
