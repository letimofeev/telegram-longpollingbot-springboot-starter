package org.telegram.telegrambot.handler;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.expection.UpdateMappingMethodValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class LongPollingBotUpdateMappingMethodApplier implements UpdateMappingMethodApplier {

    @Override
    @SuppressWarnings("unchecked")
    public List<PartialBotApiMethod<Message>> applyHandlerMappingMethod(Update update, Method method, Object handler) {
        validateMethodSignature(method);
        Object apiMethods = ReflectionUtils.invokeMethod(method, handler, update);
        Objects.requireNonNull(apiMethods);
        if (apiMethods instanceof Collection) {
            validateCollection((Collection<?>) apiMethods, method);
            return List.copyOf((Collection<? extends PartialBotApiMethod<Message>>) apiMethods);
        }
        return List.of((PartialBotApiMethod<Message>) apiMethods);
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
        if (!Update.class.isAssignableFrom(parameterType)) {
            String message = String.format("Unresolved parameter for annotated as @UpdateMapping method %s, " +
                    "expected instance of %s, found %s", method, Update.class, parameterType);
            throw new IllegalStateException(message);
        }
    }

    private void validateReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (!(PartialBotApiMethod.class.isAssignableFrom(returnType) || Collection.class.isAssignableFrom(returnType))) {
            String message = String.format("Unresolved return type for annotated as @UpdateMapping method %s, " +
                    "expected instance of %s or Collection, found %s", method, PartialBotApiMethod.class, returnType);
            throw new IllegalStateException(message);
        }
    }

    private void validateCollection(Collection<?> apiMethods, Method method) {
        for (Object apiMethod : apiMethods) {
            if (!(apiMethod instanceof PartialBotApiMethod)) {
                String message = String.format("Unresolved type %s in Collection " +
                        "for annotated as @UpdateMapping method %s", apiMethod.getClass(), method);
                throw new IllegalStateException(message);
            }
        }
    }
}
