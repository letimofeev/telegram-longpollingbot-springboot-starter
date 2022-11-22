package org.telegram.telegrambot.handler;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambot.model.UpdateMappingMethodContainer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class LongPollingBotUpdateDispatcher implements UpdateDispatcher {

    private final StateSource stateSource;
    private final UpdateMappingMethodContainer mappingMethodContainer;
    private final UpdateMappingMethodInvoker methodInvoker;
    private final BotApiMethodExecutorResolver methodExecutorResolver;

    public LongPollingBotUpdateDispatcher(StateSource stateSource, UpdateMappingMethodContainer mappingMethodContainer, UpdateMappingMethodInvoker methodInvoker, BotApiMethodExecutorResolver methodExecutorResolver) {
        this.stateSource = stateSource;
        this.mappingMethodContainer = mappingMethodContainer;
        this.methodInvoker = methodInvoker;
        this.methodExecutorResolver = methodExecutorResolver;
    }


    @Override
    public void executeHandlerOnUpdate(Update update, TelegramLongPollingBot bot) {
        long chatId = update.getMessage().getChatId();
        String state = stateSource.getState(chatId);
        Optional<MethodTargetPair> methodOptional = mappingMethodContainer.getMappingMethod(state);
        if (methodOptional.isEmpty()) {
            throw new NoUpdateHandlerFoundException("No handlers found for state: " + state);
        }
        MethodTargetPair mappingMethod = methodOptional.get();
        List<PartialBotApiMethod<Message>> apiMethods = methodInvoker.invokeHandlerMappingMethod(update, mappingMethod);
        executeAllApiMethods(apiMethods, bot);
    }

    private void executeAllApiMethods(List<PartialBotApiMethod<Message>> apiMethods, TelegramLongPollingBot bot) {
        for (PartialBotApiMethod<Message> apiMethod : apiMethods) {
            Method apiMethodExecutor = methodExecutorResolver.getApiMethodExecutionMethod(apiMethod);
            ReflectionUtils.invokeMethod(apiMethodExecutor, bot, apiMethod);
        }
    }
}
