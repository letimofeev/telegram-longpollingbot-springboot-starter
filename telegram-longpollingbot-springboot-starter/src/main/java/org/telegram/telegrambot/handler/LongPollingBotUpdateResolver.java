package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.model.InvocationUnit;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
public class LongPollingBotUpdateResolver implements UpdateResolver {

    private static final Logger log = LoggerFactory.getLogger(LongPollingBotUpdateResolver.class);

    private final UpdateMappingMethodProvider methodProvider;
    private final ApiMethodsReturningMethodInvoker methodInvoker;
    private final StateManager stateManager;

    public LongPollingBotUpdateResolver(UpdateMappingMethodProvider methodProvider, ApiMethodsReturningMethodInvoker methodInvoker, StateManager stateManager) {
        this.methodProvider = methodProvider;
        this.methodInvoker = methodInvoker;
        this.stateManager = stateManager;
    }

    @Override
    public List<? extends PartialBotApiMethod<Message>> getResponse(Update update) {
        log.debug("Resolving response for chatId: {}", update.getMessage().getChatId());
        Optional<InvocationUnit> methodOptional = methodProvider.getUpdateMappingMethod(update);
        if (methodOptional.isEmpty()) {
            throw new NoUpdateHandlerFoundException("No handlers found for update: " + update);
        }
        InvocationUnit mappingMethod = methodOptional.get();
        List<? extends PartialBotApiMethod<Message>> botApiMethods = methodInvoker.invokeMethod(mappingMethod);
        long chatId = update.getMessage().getChatId();
        stateManager.setNewStateIfRequired(chatId, mappingMethod.getMethod());
        return botApiMethods;
    }
}
