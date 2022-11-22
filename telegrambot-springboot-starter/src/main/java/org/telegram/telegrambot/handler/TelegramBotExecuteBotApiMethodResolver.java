package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.expection.BotApiMethodExecutorResolverException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.util.ReflectionUtils.getUniqueDeclaredMethods;

public class TelegramBotExecuteBotApiMethodResolver implements BotApiMethodExecutorResolver {

    private static final String TARGET_METHOD_NAME = "execute";
    private static final int REQUIRED_PARAMETERS_NUMBER = 1;

    @Override
    public Method getApiMethodExecutionMethod(PartialBotApiMethod<Message> apiMethod) {
        Class<?> requiredParameterType = resolveRequiredParameterType(apiMethod);
        List<Method> methods = stream(getUniqueDeclaredMethods(TelegramLongPollingBot.class))
                .filter(this::filterByTargetMethodName)
                .filter(this::filterByParametersNumber)
                .filter(method -> filterByParameterType(method, requiredParameterType))
                .collect(Collectors.toList());
        checkMethodsNumber(methods, apiMethod);
        return methods.get(0);
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
        if (methods.isEmpty()) {
            String message = String.format("No executors found for api method: %s", apiMethod.getClass());
            throw new BotApiMethodExecutorResolverException(message);
        } else if (methods.size() > 1) {
            String message = String.format("Found more than one executors: %s for api method: %s", methods, apiMethod.getClass());
            throw new BotApiMethodExecutorResolverException(message);
        }
    }
}
