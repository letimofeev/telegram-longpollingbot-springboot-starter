package org.telegram.telegrambot.expection;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ExceptionHandler {

    void handleException(Update update, Exception e);
}
