package org.telegram.telegrambot.model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateMappingMethodContainer {

    private final Map<String, MethodTargetPair> invokerByState = new ConcurrentHashMap<>();

    public Optional<MethodTargetPair> getMappingMethod(String state) {
        return Optional.ofNullable(invokerByState.get(state.toLowerCase()));
    }

    public void putMappingMethod(String state, MethodTargetPair invoker) {
        invokerByState.put(state.toLowerCase(), invoker);
    }
}
