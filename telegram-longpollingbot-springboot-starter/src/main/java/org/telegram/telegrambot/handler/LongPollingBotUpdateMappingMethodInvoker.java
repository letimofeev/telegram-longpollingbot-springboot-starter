package org.telegram.telegrambot.handler;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class LongPollingBotUpdateMappingMethodInvoker implements UpdateMappingMethodInvoker {

    @Override
    @SuppressWarnings("unchecked")
    public List<? extends PartialBotApiMethod<Message>> invokeUpdateMappingMethod(Update update, MethodTargetPair mappingMethod) {
        Method method = mappingMethod.getMethod();
        Object handler = mappingMethod.getTarget();
        Object apiMethods = ReflectionUtils.invokeMethod(method, handler, update);
        Objects.requireNonNull(apiMethods, String.format("@UpdateMapping method %s returned null", method));
        if (apiMethods instanceof Collection) {
            validateCollection((Collection<?>) apiMethods, method);
            return List.copyOf((Collection<? extends PartialBotApiMethod<Message>>) apiMethods);
        }
        return List.of((PartialBotApiMethod<Message>) apiMethods);
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
