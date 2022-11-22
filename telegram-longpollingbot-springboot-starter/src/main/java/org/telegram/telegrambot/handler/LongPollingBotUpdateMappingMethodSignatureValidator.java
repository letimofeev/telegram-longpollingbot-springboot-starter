package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.expection.UpdateMappingMethodSignatureValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

public class LongPollingBotUpdateMappingMethodSignatureValidator implements UpdateMappingMethodSignatureValidator {

    private static final int REQUIRED_PARAMETERS_NUMBER = 1;
    private static final Class<?> REQUIRED_PARAMETER_TYPE = Update.class;
    private static final Class<?> REQUIRED_RETURN_TYPE = PartialBotApiMethod.class;

    @Override
    public void validateMethodSignature(Method method) {
        try {
            validateParametersNumber(method);
            validateParameterType(method);
            validateReturnType(method);
        } catch (Exception e) {
            String message = String.format("Exception during validating update handler method, nested exception: %s", e);
            throw new UpdateMappingMethodSignatureValidationException(message, e);
        }
    }

    private void validateParametersNumber(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length != REQUIRED_PARAMETERS_NUMBER) {
            String message = String.format("Method %s annotated as @UpdateMapping should have 1 parameter", method);
            throw new IllegalStateException(message);
        }
    }

    private void validateParameterType(Method method) {
        Parameter[] parameters = method.getParameters();
        Parameter parameter = parameters[0];
        Class<?> parameterType = parameter.getType();
        if (!REQUIRED_PARAMETER_TYPE.isAssignableFrom(parameterType)) {
            String message = String.format("Unresolved parameter for annotated as @UpdateMapping method %s, " +
                    "expected instance of %s, found %s", method, Update.class, parameterType);
            throw new IllegalStateException(message);
        }
    }

    private void validateReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (!(REQUIRED_RETURN_TYPE.isAssignableFrom(returnType) || Collection.class.isAssignableFrom(returnType))) {
            String message = String.format("Unresolved return type for annotated as @UpdateMapping method %s, " +
                    "expected instance of %s or Collection, found %s", method, PartialBotApiMethod.class, returnType);
            throw new IllegalStateException(message);
        }
    }
}
