package org.telegram.telegrambot.expection.handler;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.bot.LongPollingBot;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class SendingExceptionMappingMethodInvoker implements ExceptionMappingMethodInvoker {

    private final LongPollingBot bot;

    public SendingExceptionMappingMethodInvoker(LongPollingBot bot) {
        this.bot = bot;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invokeExceptionMappingMethod(MethodTargetPair exceptionMapping, Update update, Exception e) {
        Method method = exceptionMapping.getMethod();
        Object target = exceptionMapping.getTarget();
        Object mappingMethodResult = ReflectionUtils.invokeMethod(method, target, update, e);
        if (mappingMethodResult == null) {
            return;
        }
        if (isSendingResponsesCollectionRequired(mappingMethodResult)) {
            List<? extends PartialBotApiMethod<Message>> apiMethods = List.copyOf((Collection<? extends PartialBotApiMethod<Message>>) mappingMethodResult);
            bot.executeAllApiMethods(apiMethods);
        } else if (isSendingResponseRequired(mappingMethodResult)) {
            PartialBotApiMethod<Message> apiMethod = (PartialBotApiMethod<Message>) mappingMethodResult;
            bot.executeApiMethod(apiMethod);
        }
    }

    private boolean isSendingResponsesCollectionRequired(Object mappingMethodResult) {
        if (mappingMethodResult instanceof Collection) {
            Collection<?> collection = (Collection<?>) mappingMethodResult;
            for (Object o : collection) {
                if (!(o instanceof PartialBotApiMethod)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isSendingResponseRequired(Object mappingMethodResult) {
        return mappingMethodResult instanceof PartialBotApiMethod;
    }
}
