package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.expection.BotApiMethodExecutorResolverException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.util.ReflectionUtils.getUniqueDeclaredMethods;

@Component
public class LongPollingBotApiMethodExecutorResolver implements BotApiMethodExecutorResolver {

    private static final Logger log = LoggerFactory.getLogger(LongPollingBotApiMethodExecutorResolver.class);

    private static final String TARGET_METHOD_NAME = "execute";
    private static final int REQUIRED_PARAMETERS_NUMBER = 1;

    @Override
    public Method getApiMethodExecutionMethod(PartialBotApiMethod<Message> apiMethod) {
        log.debug("Looking for execute() method in TelegramLongPollingBot with argument type: {}",
                apiMethod.getClass().getName());

        Class<?> requiredParameterType = resolveRequiredParameterType(apiMethod);
        List<Method> methods = stream(getUniqueDeclaredMethods(TelegramLongPollingBot.class))
                .filter(this::filterByTargetMethodName)
                .filter(this::filterByParametersNumber)
                .filter(method -> filterByParameterType(method, requiredParameterType))
                .collect(Collectors.toList());
        checkMethodsNumber(methods, apiMethod);
        Method executeMethod = methods.get(0);

        log.trace("Found execute() method: {}", executeMethod);
        return executeMethod;
    }

    private boolean filterByTargetMethodName(Method method) {
        String methodName = method.getName();
        return methodName.equals(TARGET_METHOD_NAME);
    }

    private boolean filterByParametersNumber(Method method) {
        Parameter[] parameters = method.getParameters();
        return parameters.length == REQUIRED_PARAMETERS_NUMBER;
    }

    private boolean filterByParameterType(Method method, Class<?> requiredType) {
        Parameter parameter = method.getParameters()[0];
        return parameter.getType() == requiredType;
    }

    private Class<?> resolveRequiredParameterType(PartialBotApiMethod<Message> apiMethod) {
        if (apiMethod instanceof BotApiMethod<?>) {
            return BotApiMethod.class;
        } else {
            return apiMethod.getClass();
        }
    }

    private void checkMethodsNumber(List<Method> methods, PartialBotApiMethod<Message> apiMethod) {
        Class<? extends PartialBotApiMethod> apiMethodClass = apiMethod.getClass();
        if (methods.isEmpty()) {
            String message = String.format("No executors found for api method: %s", apiMethodClass);
            throw new BotApiMethodExecutorResolverException(message);
        } else if (methods.size() > 1) {
            String message = String.format("Found more than one execute methods: %s for api method: %s", methods, apiMethodClass.getName());
            throw new BotApiMethodExecutorResolverException(message);
        }
    }
}
