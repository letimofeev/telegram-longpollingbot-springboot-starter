package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Optional;

public class LongPollingBotUpdateMappingMethodSelector implements UpdateMappingMethodSelector {

    @Override
    public Optional<Method> lookupHandlerMappingMethod(String state, Object handler) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        for (Method method : methods) {
            UpdateMapping annotation = method.getAnnotation(UpdateMapping.class);
            if (annotation != null) {
                if (state.equalsIgnoreCase(annotation.state())) {
                    return Optional.of(method);
                }
            }
        }
        return Optional.empty();
    }
}
