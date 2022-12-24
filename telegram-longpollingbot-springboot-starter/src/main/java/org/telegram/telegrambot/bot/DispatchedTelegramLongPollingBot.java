package org.telegram.telegrambot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.handler.update.BotUpdateDispatcher;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;

public class DispatchedTelegramLongPollingBot extends TelegramLongPollingBotExtended {

    private static final Logger log = LoggerFactory.getLogger(DispatchedTelegramLongPollingBot.class);

    private final String botUsername;
    private final String botToken;
    private final BotUpdateDispatcher updateDispatcher;
    private final BotApiMethodExecutorResolver methodExecutorResolver;

    public DispatchedTelegramLongPollingBot(String botUsername, String botToken,
                                            BotUpdateDispatcher updateDispatcher,
                                            BotApiMethodExecutorResolver methodExecutorResolver) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.updateDispatcher = updateDispatcher;
        this.methodExecutorResolver = methodExecutorResolver;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received update: {}", update);
        updateDispatcher.doDispatch(update, this);
    }

    @Override
    public void executeApiMethod(PartialBotApiMethod<Message> apiMethod) {
        log.trace("Executing api method: {}", apiMethod);
        Method apiMethodExecutor = methodExecutorResolver.getApiMethodExecutionMethod(apiMethod);
        ReflectionUtils.invokeMethod(apiMethodExecutor, this, apiMethod);
    }

    @Override
    public void executeAllApiMethods(List<? extends PartialBotApiMethod<Message>> apiMethods) {
        for (PartialBotApiMethod<Message> apiMethod : apiMethods) {
            executeApiMethod(apiMethod);
        }
    }
}
