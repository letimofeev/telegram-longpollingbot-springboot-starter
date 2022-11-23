package org.telegram.telegrambot.bot;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.handler.UpdateDispatcher;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;

public class DispatchedTelegramLongPollingBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final UpdateDispatcher updateDispatcher;
    private final BotApiMethodExecutorResolver methodExecutorResolver;

    public DispatchedTelegramLongPollingBot(String botUsername, String botToken, UpdateDispatcher updateDispatcher, BotApiMethodExecutorResolver methodExecutorResolver) {
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
        updateDispatcher.executeHandlerOnUpdate(update, this);
    }

    @Override
    public void executeAllApiMethods(List<PartialBotApiMethod<Message>> apiMethods) {
        for (PartialBotApiMethod<Message> apiMethod : apiMethods) {
            Method apiMethodExecutor = methodExecutorResolver.getApiMethodExecutionMethod(apiMethod);
            ReflectionUtils.invokeMethod(apiMethodExecutor, this, apiMethod);
        }
    }
}
