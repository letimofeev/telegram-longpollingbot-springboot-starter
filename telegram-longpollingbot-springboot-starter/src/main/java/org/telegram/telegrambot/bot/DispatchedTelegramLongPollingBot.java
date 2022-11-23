package org.telegram.telegrambot.bot;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.handler.UpdateResolver;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;

public class DispatchedTelegramLongPollingBot extends TelegramLongPollingBotExtended {

    private final String botUsername;
    private final String botToken;
    private final UpdateResolver updateResolver;
    private final BotApiMethodExecutorResolver methodExecutorResolver;

    public DispatchedTelegramLongPollingBot(String botUsername, String botToken, UpdateResolver updateResolver, BotApiMethodExecutorResolver methodExecutorResolver) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.updateResolver = updateResolver;
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
        List<? extends PartialBotApiMethod<Message>> apiMethods = updateResolver.getResponse(update);
        executeAllApiMethods(apiMethods);
    }

    @Override
    public void executeApiMethod(PartialBotApiMethod<Message> apiMethod) {
        Method apiMethodExecutor = methodExecutorResolver.getApiMethodExecutionMethod(apiMethod);
        ReflectionUtils.invokeMethod(apiMethodExecutor, this, apiMethod);
    }

    @Override
    public void executeAllApiMethods(List<? extends PartialBotApiMethod<Message>> apiMethods) {
        for (PartialBotApiMethod<Message> apiMethod : apiMethods) {
            Method apiMethodExecutor = methodExecutorResolver.getApiMethodExecutionMethod(apiMethod);
            ReflectionUtils.invokeMethod(apiMethodExecutor, this, apiMethod);
        }
    }
}
