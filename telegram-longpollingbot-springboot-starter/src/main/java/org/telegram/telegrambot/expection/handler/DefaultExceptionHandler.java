package org.telegram.telegrambot.expection.handler;

import org.telegram.telegrambot.annotation.ExceptionHandler;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExceptionHandler
public class DefaultExceptionHandler {

    private final String message;

    public DefaultExceptionHandler(String message) {
        this.message = message;
    }

    @ExceptionMapping
    public SendMessage handleException(Update update, Exception e) {
        long chatId = update.getMessage().getChatId();
        return SendMessage.builder().chatId(chatId).text(message).build();
    }
}
