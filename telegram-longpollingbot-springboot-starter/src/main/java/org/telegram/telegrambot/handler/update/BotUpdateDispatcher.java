package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.bot.TelegramLongPollingBotExtended;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class BotUpdateDispatcher {

    private final UpdateHandlerResolver updateHandlerResolver;
    private final UpdateMappingMethodInvoker methodInvoker;

    public BotUpdateDispatcher(UpdateHandlerResolver updateHandlerResolver,
                               UpdateMappingMethodInvoker methodInvoker) {
        this.updateHandlerResolver = updateHandlerResolver;
        this.methodInvoker = methodInvoker;
    }

    public void doDispatch(Update update, TelegramLongPollingBotExtended bot) {
        UpdateHandlerExecutionChain executionChain = updateHandlerResolver.getHandler(update);
        executionChain.applyPreHandle(update);
        InvocableUpdateMappingMethod mappingMethod = executionChain.getMappingMethod();
        List<? extends PartialBotApiMethod<Message>> apiMethods = methodInvoker.invokeMethod(mappingMethod);
        bot.executeAllApiMethods(apiMethods);
        executionChain.applyPostHandle(update);
    }
}
