package org.telegram.telegrambot.model;

import org.telegram.telegrambot.expection.ExceptionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionHandlerContainer {

    private final Map<Class<? extends Exception>, ExceptionHandler> exceptionHandlerByExceptionType = new ConcurrentHashMap<>();

    public Optional<ExceptionHandler> getExceptionHandler(Class<? extends Exception> exceptionType) {
        ExceptionHandler exceptionHandler = exceptionHandlerByExceptionType.get(exceptionType);
        if (exceptionHandler == null) {
            Optional<Class<? extends Exception>> parentType = exceptionHandlerByExceptionType.keySet().stream()
                    .filter(currentType -> currentType.isAssignableFrom(exceptionType))
                    .findFirst();
            if (parentType.isPresent()) {
                return Optional.ofNullable(exceptionHandlerByExceptionType.get(parentType.get()));
            }
        }
        return Optional.empty();
    }

    public void putExceptionHandler(Class<? extends Exception> exceptionType, ExceptionHandler exceptionHandler) {
        exceptionHandlerByExceptionType.put(exceptionType, exceptionHandler);
    }
}
