package org.telegram.telegrambot.handler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateDispatcher {

    void executeHandlerOnUpdate(Update update, TelegramLongPollingBot bot);
}
