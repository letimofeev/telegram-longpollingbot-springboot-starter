package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.Optional;

@Component
public class UpdateMappingMethodContainer extends MethodTargetPairContainer<String> {

    public Optional<MethodTargetPair> getUpdateMapping(String state) {
        return Optional.ofNullable(methodTargetPairs.get(state.toLowerCase()));
    }

    public void putUpdateMapping(String state, MethodTargetPair methodTargetPair) {
        methodTargetPairs.put(state.toLowerCase(), methodTargetPair);
    }
}
