package org.telegram.telegrambot.repository;

import org.telegram.telegrambot.model.UpdateMappingMethod;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateMappingMethodContainer {

    private final Map<String, UpdateMappingMethod> invokerByState = new ConcurrentHashMap<>();

    public Optional<UpdateMappingMethod> getMappingMethod(String state) {
        return Optional.ofNullable(invokerByState.get(state.toLowerCase()));
    }

    public void putMappingMethod(String state, UpdateMappingMethod invoker) {
        invokerByState.put(state.toLowerCase(), invoker);
    }
}
