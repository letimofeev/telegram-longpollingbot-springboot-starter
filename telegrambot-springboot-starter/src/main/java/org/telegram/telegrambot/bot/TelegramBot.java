package org.telegram.telegrambot.bot;

import org.telegram.telegrambot.handler.UpdateDispatcher;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;

    private final TelegramBotsApi telegramBotsApi;
    private final UpdateDispatcher updateDispatcher;

    public TelegramBot(String botUsername, String botToken,
                       TelegramBotsApi telegramBotsApi, UpdateDispatcher updateDispatcher) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.telegramBotsApi = telegramBotsApi;
        this.updateDispatcher = updateDispatcher;
    }

    @PostConstruct
    public void register() throws TelegramApiException {
        telegramBotsApi.registerBot(this);
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
}
