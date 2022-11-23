package org.telegram.telegrambot.handler;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ApiMethodsReturningMethodInvokerImpl implements ApiMethodsReturningMethodInvoker {

    @SuppressWarnings("unchecked")
    public List<? extends PartialBotApiMethod<Message>> invokeMethod(MethodTargetPair methodTargetPair, Object... args) {
        Method method = methodTargetPair.getMethod();
        Object handler = methodTargetPair.getTarget();
        Object apiMethods = ReflectionUtils.invokeMethod(method, handler, args);
        Objects.requireNonNull(apiMethods, String.format("Method supposed to return api methods %s returned null", method));
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
                        "for method %s", apiMethod.getClass(), method);
                throw new IllegalStateException(message);
            }
        }
    }
}
