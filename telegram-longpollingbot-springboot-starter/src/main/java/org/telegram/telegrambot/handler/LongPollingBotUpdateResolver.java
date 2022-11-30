package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambot.handler.update.UpdateMappingMethodProviderResolver;
import org.telegram.telegrambot.handler.update.UpdateType;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class LongPollingBotUpdateResolver implements UpdateResolver {

    private static final Logger log = LoggerFactory.getLogger(LongPollingBotUpdateResolver.class);

    private final UpdateMappingMethodProviderResolver methodProviderResolver;
    private final ApiMethodsReturningMethodInvoker methodInvoker;
    private final StateManager stateManager;

    public LongPollingBotUpdateResolver(UpdateMappingMethodProviderResolver methodProviderResolver,
                                        ApiMethodsReturningMethodInvoker methodInvoker,
                                        StateManager stateManager) {
        this.methodProviderResolver = methodProviderResolver;
        this.methodInvoker = methodInvoker;
        this.stateManager = stateManager;
    }


    @Override
    public List<? extends PartialBotApiMethod<Message>> getResponse(Update update) {
        log.debug("Resolving response for update: {}", update);

        if (update.hasMessage()) {
            InvocationUnit mappingMethod = methodProviderResolver.getUpdateMappingMethod(update.getMessage(), UpdateType.MESSAGE)
                    .orElseThrow(() -> new NoUpdateHandlerFoundException("No handlers found for update: " + update));
            List<? extends PartialBotApiMethod<Message>> botApiMethods = methodInvoker.invokeMethod(mappingMethod);
            long chatId = update.getMessage().getChatId();
            stateManager.setNewStateIfRequired(chatId, mappingMethod.getMethod());
            return botApiMethods;
        }
        throw new UnsupportedOperationException();
    }
}
