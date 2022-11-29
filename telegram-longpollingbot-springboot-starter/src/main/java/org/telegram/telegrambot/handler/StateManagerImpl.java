package org.telegram.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.repository.BotStateSource;

import java.lang.reflect.Method;

@Component
public class StateManagerImpl implements StateManager {

    private final BotStateSource botStateSource;

    public StateManagerImpl(BotStateSource botStateSource) {
        this.botStateSource = botStateSource;
    }

    @Override
    public void setNewStateIfRequired(long chatId, Method updateMappingMethod) {
        UpdateMapping annotation = updateMappingMethod.getAnnotation(UpdateMapping.class);
        if (annotation != null) {
            String newState = annotation.newState();
            if (!newState.isEmpty()) {
                botStateSource.setState(chatId, newState);
            }
        }
    }
}
