package org.telegram.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Component
public class TelegramBotUpdateDispatcher implements UpdateDispatcher {

    private final List<Object> handlers;
    private final UpdateMappingMethodSelector methodSelector;
    private final UpdateMappingMethodApplier methodApplier;
    private final BotApiMethodExecutorResolver methodExecutorResolver;

    public TelegramBotUpdateDispatcher(List<Object> handlers, UpdateMappingMethodSelector methodSelector,
                                       UpdateMappingMethodApplier methodApplier, BotApiMethodExecutorResolver methodExecutorResolver) {
        this.handlers = handlers;
        this.methodSelector = methodSelector;
        this.methodApplier = methodApplier;
        this.methodExecutorResolver = methodExecutorResolver;
    }

    @Override
    public void executeHandlerOnUpdate(Update update, TelegramLongPollingBot bot) {
        for (Object handler : handlers) {
            Optional<Method> methodOptional = methodSelector.lookupHandlerMappingMethod(update, handler);
            if (methodOptional.isPresent()) {
                Method method = methodOptional.get();
                PartialBotApiMethod<Message> responseApiMethod = methodApplier.applyHandlerMappingMethod(update, method, handler);
                Method apiMethodExecutor = methodExecutorResolver.getApiMethodExecutionMethod(responseApiMethod);
                ReflectionUtils.invokeMethod(apiMethodExecutor, bot, responseApiMethod);
            }
        }
        throw new NoUpdateHandlerFoundException("No handlers found for update: " + update);
    }
}
