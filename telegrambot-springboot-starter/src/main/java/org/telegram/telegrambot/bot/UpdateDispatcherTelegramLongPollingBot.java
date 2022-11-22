package org.telegram.telegrambot.bot;

import org.telegram.telegrambot.handler.UpdateDispatcher;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateDispatcherTelegramLongPollingBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final UpdateDispatcher updateDispatcher;

    public UpdateDispatcherTelegramLongPollingBot(String botUsername, String botToken, UpdateDispatcher updateDispatcher) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.updateDispatcher = updateDispatcher;
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
