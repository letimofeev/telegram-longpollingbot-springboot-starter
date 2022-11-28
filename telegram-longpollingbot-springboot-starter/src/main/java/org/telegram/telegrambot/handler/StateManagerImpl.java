package org.telegram.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.repository.StateSource;

import java.lang.reflect.Method;

@Component
public class StateManagerImpl implements StateManager {

    private final StateSource stateSource;

    public StateManagerImpl(StateSource stateSource) {
        this.stateSource = stateSource;
    }

    @Override
    public void setNewStateIfRequired(long chatId, Method updateMappingMethod) {
        UpdateMapping annotation = updateMappingMethod.getAnnotation(UpdateMapping.class);
        if (annotation != null) {
            String newState = annotation.newState();
            if (!newState.isEmpty()) {
                stateSource.setState(chatId, newState);
            }
        }
    }
}
