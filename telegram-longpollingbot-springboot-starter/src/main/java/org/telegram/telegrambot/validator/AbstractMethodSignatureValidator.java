package org.telegram.telegrambot.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambot.expection.MethodSignatureValidationException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public abstract class AbstractMethodSignatureValidator implements MethodSignatureValidator {

    private static final Logger log = LoggerFactory.getLogger(AbstractMethodSignatureValidator.class);

    protected void validateParametersNumber(Method method, int requiredParameterNumber) {
        log.trace("Validation method {} parameters number, excepted: {}", method, requiredParameterNumber);
        Parameter[] parameters = method.getParameters();
        if (parameters.length != requiredParameterNumber) {
            String message = String.format("Method %s must have %d parameters", method.getName(), requiredParameterNumber);
            throw new MethodSignatureValidationException(message);
        }
    }

    protected void validateParametersTypes(Method method, Class<?>[] requiredParametersTypes) {
        log.trace("Validation method {} parameters types, expected instances of classes: {}",
                method, Arrays.toString(requiredParametersTypes));
        Parameter[] parameters = method.getParameters();
        if (method.getParameterCount() != requiredParametersTypes.length) {
            String message = String.format("Unresolved parameters count for annotated method %s, " +
                            "expected that parameters are instances of classes: %s",
                    method.getName(), Arrays.toString(requiredParametersTypes));
            throw new MethodSignatureValidationException(message);
        }
        for (int i = 0; i < method.getParameterCount(); i++) {
            Class<?> parameterType = parameters[i].getType();
            Class<?> requiredParameterType = requiredParametersTypes[i];
            if (!requiredParameterType.isAssignableFrom(parameterType)) {
                String message = String.format("Unresolved parameter for annotated method %s, " +
                                "expected that parameters are instances of classes: %s",
                        method.getName(), Arrays.toString(requiredParametersTypes));
                throw new MethodSignatureValidationException(message);
            }
        }
    }

    protected void validateReturnType(Method method, Class<?>[] allowedReturnTypes) {
        log.trace("Validation method {} parameters number, excepted instance of one of the classes: {}",
                method, Arrays.toString(allowedReturnTypes));
        Class<?> returnType = method.getReturnType();
        for (Class<?> allowedReturnType : allowedReturnTypes) {
            if (allowedReturnType.isAssignableFrom(returnType)) {
                return;
            }
        }
        String message = String.format("Unresolved return type method %s, expected an instance of one of classes: %s",
                method.getName(), Arrays.toString(allowedReturnTypes));
        throw new MethodSignatureValidationException(message);
    }
}
