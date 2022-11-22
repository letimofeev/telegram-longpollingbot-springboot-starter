package org.telegram.telegrambot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Optional;

public interface UpdateMappingMethodSelector {

    Optional<Method> lookupHandlerMappingMethod(String state, Object handler);
}
