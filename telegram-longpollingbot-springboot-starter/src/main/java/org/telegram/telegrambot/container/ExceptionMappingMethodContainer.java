package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.model.MethodTargetPair;

import java.util.Optional;

@Component
public class ExceptionMappingMethodContainer extends AbstractContainer<Class<? extends Exception>, MethodTargetPair> {

    public Optional<MethodTargetPair> getMappingForExceptionAssignableFrom(Class<? extends Exception> exceptionType) {
        MethodTargetPair methodTargetPair = container.get(exceptionType);
        if (methodTargetPair == null) {
            Optional<Class<? extends Exception>> parentType = container.keySet().stream()
                    .filter(currentType -> currentType.isAssignableFrom(exceptionType))
                    .findFirst();
            return parentType.map(container::get);
        }
        return Optional.of(methodTargetPair);
    }
}
