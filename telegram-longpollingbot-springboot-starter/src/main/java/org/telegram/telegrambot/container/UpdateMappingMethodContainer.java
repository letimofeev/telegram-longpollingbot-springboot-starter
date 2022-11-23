package org.telegram.telegrambot.container;

import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateMappingMethodContainer {

    private final Map<String, MethodTargetPair> methodTargetPairByState = new ConcurrentHashMap<>();

    public Optional<MethodTargetPair> getMappingMethod(String state) {
        return Optional.ofNullable(methodTargetPairByState.get(state.toLowerCase()));
    }

    public void putMappingMethod(String state, MethodTargetPair methodTargetPair) {
        methodTargetPairByState.put(state.toLowerCase(), methodTargetPair);
    }
}
