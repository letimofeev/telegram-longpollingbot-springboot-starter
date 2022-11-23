package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.bot.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateDispatcher {

    void executeHandlerOnUpdate(Update update, TelegramLongPollingBot bot);
}
