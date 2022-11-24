package org.telegram.telegrambot.container;

import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MethodTargetPairContainer<T> {

    protected final Map<T, MethodTargetPair> methodTargetPairs = new ConcurrentHashMap<>();

    public Set<Map.Entry<T, MethodTargetPair>> getEntrySet() {
        return methodTargetPairs.entrySet();
    }
}
