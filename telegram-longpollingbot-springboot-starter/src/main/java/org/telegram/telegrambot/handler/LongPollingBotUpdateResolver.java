package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.dto.InvocationUnit;
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

    public LongPollingBotUpdateResolver(UpdateMappingMethodProvider methodProvider, ApiMethodsReturningMethodInvoker methodInvoker) {
        this.methodProvider = methodProvider;
        this.methodInvoker = methodInvoker;
    }

    @Override
    public List<? extends PartialBotApiMethod<Message>> getResponse(Update update) {
        log.debug("Resolving response for chatId: {}", update.getMessage().getChatId());
        Optional<InvocationUnit> methodOptional = methodProvider.getUpdateMappingMethod(update);
        if (methodOptional.isEmpty()) {
            throw new NoUpdateHandlerFoundException("No handlers found for update: " + update);
        }
        InvocationUnit mappingMethod = methodOptional.get();
        return methodInvoker.invokeMethod(mappingMethod);
    }
}
