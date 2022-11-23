package org.telegram.telegrambot.container;

import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMappingMethodContainer {

    private final Map<Class<? extends Exception>, MethodTargetPair> exceptionHandlerByExceptionType = new ConcurrentHashMap<>();

    public Optional<MethodTargetPair> getExceptionMapping(Class<? extends Exception> exceptionType) {
        MethodTargetPair methodTargetPair = exceptionHandlerByExceptionType.get(exceptionType);
        if (methodTargetPair == null) {
            Optional<Class<? extends Exception>> parentType = exceptionHandlerByExceptionType.keySet().stream()
                    .filter(currentType -> currentType.isAssignableFrom(exceptionType))
                    .findFirst();
            return parentType.map(exceptionHandlerByExceptionType::get);
        }
        return Optional.of(methodTargetPair);
    }

    public void putExceptionMapping(Class<? extends Exception> exceptionType, MethodTargetPair methodTargetPair) {
        exceptionHandlerByExceptionType.put(exceptionType, methodTargetPair);
    }
}
