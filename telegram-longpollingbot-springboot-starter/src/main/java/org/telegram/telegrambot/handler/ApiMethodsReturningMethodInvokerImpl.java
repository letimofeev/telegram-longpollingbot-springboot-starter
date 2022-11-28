package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class ApiMethodsReturningMethodInvokerImpl implements ApiMethodsReturningMethodInvoker {

    private static final Logger log = LoggerFactory.getLogger(ApiMethodsReturningMethodInvokerImpl.class);

    @SuppressWarnings("unchecked")
    public List<? extends PartialBotApiMethod<Message>> invokeMethod(InvocationUnit invocationUnit) {
        Method method = invocationUnit.getMethod();
        Object handler = invocationUnit.getTarget();
        Object[] args = invocationUnit.getArgs();

        log.debug("Invoking method: {} with args: {}", method, Arrays.toString(args));

        Object apiMethods = ReflectionUtils.invokeMethod(method, handler, args);
        Objects.requireNonNull(apiMethods, String.format("Method supposed to return api methods %s returned null", method));

        if (apiMethods instanceof Collection) {
            log.trace("Method {} returned collection of api methods", method);
            return List.copyOf((Collection<? extends PartialBotApiMethod<Message>>) apiMethods);
        }
        log.trace("Method {} returned single api method", method);
        return List.of((PartialBotApiMethod<Message>) apiMethods);
    }
}
