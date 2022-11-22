package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.state.StateSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Optional;

public class LongPollingBotUpdateMappingMethodSelector implements UpdateMappingMethodSelector {

    private final StateSource stateSource;

    public LongPollingBotUpdateMappingMethodSelector(StateSource stateSource) {
        this.stateSource = stateSource;
    }

    @Override
    public Optional<Method> lookupHandlerMappingMethod(Update update, Object handler) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        for (Method method : methods) {
            UpdateMapping annotation = method.getAnnotation(UpdateMapping.class);
            if (annotation != null) {
                long chatId = update.getMessage().getChatId();
                String state = stateSource.getState(chatId);
                if (state.equalsIgnoreCase(annotation.state())) {
                    return Optional.of(method);
                }
            }
        }
        return Optional.empty();
    }
}
