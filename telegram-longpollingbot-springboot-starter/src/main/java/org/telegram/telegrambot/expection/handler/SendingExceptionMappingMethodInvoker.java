package org.telegram.telegrambot.expection.handler;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

public class SendingExceptionMappingMethodInvoker implements ExceptionMappingMethodInvoker {

    private static final Class<?> SENDING_RETURN_TYPE = PartialBotApiMethod.class;

    private final TelegramLongPollingBot bot;
    private final BotApiMethodExecutorResolver botApiMethodExecutorResolver;

    public SendingExceptionMappingMethodInvoker(TelegramLongPollingBot bot,
                                                BotApiMethodExecutorResolver botApiMethodExecutorResolver) {
        this.bot = bot;
        this.botApiMethodExecutorResolver = botApiMethodExecutorResolver;
    }

    @Override
    public void invokeExceptionMappingMethod(MethodTargetPair exceptionMapping, Update update, Exception e) {
        Method method = exceptionMapping.getMethod();
        Object target = exceptionMapping.getTarget();
        Object exceptionMappingMethodResult = ReflectionUtils.invokeMethod(method, target, update, e);
        if (isSendingResponseRequired(method)) {
            @SuppressWarnings("unchecked")
            PartialBotApiMethod<Message> message = (PartialBotApiMethod<Message>) exceptionMappingMethodResult;
            Method apiMethodExecutionMethod = botApiMethodExecutorResolver.getApiMethodExecutionMethod(message);
            ReflectionUtils.invokeMethod(apiMethodExecutionMethod, bot, message);
        }
    }

    private boolean isSendingResponseRequired(Method method) {
        Class<?> returnType = method.getReturnType();
        return SENDING_RETURN_TYPE.isAssignableFrom(returnType);
    }
}
