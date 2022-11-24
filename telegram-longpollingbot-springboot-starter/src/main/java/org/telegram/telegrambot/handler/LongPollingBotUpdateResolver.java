package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.UpdateMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
public class LongPollingBotUpdateResolver implements UpdateResolver {

    private static final Logger log = LoggerFactory.getLogger(LongPollingBotUpdateResolver.class);

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
        log.debug("Resolving response for state: \"{}\" and update: {}", state, update);
        Optional<MethodTargetPair> methodOptional = mappingMethodContainer.getUpdateMappingIgnoringCase(state);
        if (methodOptional.isEmpty()) {
            throw new NoUpdateHandlerFoundException("No handlers found for state: " + state);
        }
        MethodTargetPair mappingMethod = methodOptional.get();
        log.trace("Found method: {} for state: {}", mappingMethod.getMethod(), state);
        return methodInvoker.invokeMethod(mappingMethod, update);
    }
}
