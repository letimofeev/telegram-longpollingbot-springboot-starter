package org.telegram.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.expection.UpdateMappingMethodValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
public class TelegramBotUpdateMappingMethodApplier implements UpdateMappingMethodApplier {

    @Override
    @SuppressWarnings("unchecked")
    public PartialBotApiMethod<Message> applyHandlerMappingMethod(Update update, Method method, Object handler) {
        validateMethodSignature(method);
        return (PartialBotApiMethod<Message>) ReflectionUtils.invokeMethod(method, handler, update);
    }

    private void validateMethodSignature(Method method) {
        try {
            validateParametersNumber(method);
            validateParameterType(method);
            validateReturnType(method);
        } catch (Exception e) {
            String message = String.format("Exception during validating update handler method, nested exception: %s", e);
            throw new UpdateMappingMethodValidationException(message, e);
        }
    }

    private void validateParametersNumber(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 1) {
            String message = String.format("Method %s annotated as @UpdateMapping should have 1 parameter", method);
            throw new IllegalStateException(message);
        }
    }

    private void validateParameterType(Method method) {
        Parameter[] parameters = method.getParameters();
        Parameter parameter = parameters[0];
        Class<?> parameterType = parameter.getType();
        if (!parameterType.isAssignableFrom(Update.class)) {
            String message = String.format("Unresolved parameter for annotated as @UpdateMapping method %s, " +
                    "expected instance of %s, found %s", method, Update.class, parameterType);
            throw new IllegalStateException(message);
        }
    }

    private void validateReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (!returnType.isAssignableFrom(PartialBotApiMethod.class)) {
            String message = String.format("Unresolved return type for annotated as @UpdateMapping method %s, " +
                    "expected instance of %s, found %s", method, PartialBotApiMethod.class, returnType);
            throw new IllegalStateException(message);
        }
    }
}
