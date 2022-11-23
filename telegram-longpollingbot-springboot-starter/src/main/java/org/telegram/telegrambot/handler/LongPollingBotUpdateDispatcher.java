package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.bot.LongPollingBot;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambot.model.UpdateMappingMethodContainer;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

public class LongPollingBotUpdateDispatcher implements UpdateDispatcher {

    private final StateSource stateSource;
    private final UpdateMappingMethodContainer mappingMethodContainer;
    private final UpdateMappingMethodInvoker methodInvoker;

    public LongPollingBotUpdateDispatcher(StateSource stateSource, UpdateMappingMethodContainer mappingMethodContainer, UpdateMappingMethodInvoker methodInvoker) {
        this.stateSource = stateSource;
        this.mappingMethodContainer = mappingMethodContainer;
        this.methodInvoker = methodInvoker;
    }

    @Override
    public void executeHandlerOnUpdate(Update update, LongPollingBot bot) {
        long chatId = update.getMessage().getChatId();
        String state = stateSource.getState(chatId);
        Optional<MethodTargetPair> methodOptional = mappingMethodContainer.getMappingMethod(state);
        if (methodOptional.isEmpty()) {
            throw new NoUpdateHandlerFoundException("No handlers found for state: " + state);
        }
        MethodTargetPair mappingMethod = methodOptional.get();
        List<PartialBotApiMethod<Message>> apiMethods = methodInvoker.invokeUpdateMappingMethod(update, mappingMethod);
        bot.executeAllApiMethods(apiMethods);
    }
}
