package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.Optional;

@Component
public class ExceptionMappingMethodContainer extends MethodTargetPairContainer<Class<? extends Exception>> {

    public Optional<MethodTargetPair> getExceptionMapping(Class<? extends Exception> exceptionType) {
        MethodTargetPair methodTargetPair = methodTargetPairs.get(exceptionType);
        if (methodTargetPair == null) {
            Optional<Class<? extends Exception>> parentType = methodTargetPairs.keySet().stream()
                    .filter(currentType -> currentType.isAssignableFrom(exceptionType))
                    .findFirst();
            return parentType.map(methodTargetPairs::get);
        }
        return Optional.of(methodTargetPair);
    }

    public Optional<MethodTargetPair> getExactExceptionMapping(Class<? extends Exception> exceptionType) {
        return getMethodTargetPair(exceptionType);
    }

    public void putExceptionMapping(Class<? extends Exception> exceptionType, MethodTargetPair methodTargetPair) {
        putMethodTargetPair(exceptionType, methodTargetPair);
    }
}
