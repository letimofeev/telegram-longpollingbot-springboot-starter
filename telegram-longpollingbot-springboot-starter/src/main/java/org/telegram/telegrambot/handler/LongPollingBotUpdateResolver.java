package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambot.model.UpdateMappingMethodContainer;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

public class LongPollingBotUpdateResolver implements UpdateResolver {

    private final StateSource stateSource;
    private final UpdateMappingMethodContainer mappingMethodContainer;
    private final ApiMethodsReturningMethodInvoker methodInvoker;

    public LongPollingBotUpdateResolver(StateSource stateSource, UpdateMappingMethodContainer mappingMethodContainer, ApiMethodsReturningMethodInvoker methodInvoker) {
        this.stateSource = stateSource;
        this.mappingMethodContainer = mappingMethodContainer;
        this.methodInvoker = methodInvoker;
    }

    @Override
    public List<? extends PartialBotApiMethod<Message>> getResponse(Update update) {
        long chatId = update.getMessage().getChatId();
        String state = stateSource.getState(chatId);
        Optional<MethodTargetPair> methodOptional = mappingMethodContainer.getMappingMethod(state);
        if (methodOptional.isEmpty()) {
            throw new NoUpdateHandlerFoundException("No handlers found for state: " + state);
        }
        MethodTargetPair mappingMethod = methodOptional.get();
        return methodInvoker.invokeMethod(mappingMethod, update);
    }
}
