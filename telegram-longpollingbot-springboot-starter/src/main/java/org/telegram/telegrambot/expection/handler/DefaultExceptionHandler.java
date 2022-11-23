package org.telegram.telegrambot.expection.handler;

import org.telegram.telegrambot.annotation.ExceptionHandler;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExceptionHandler
public class DefaultExceptionHandler {

    private final String message;
    private final TelegramLongPollingBot bot;

    public DefaultExceptionHandler(String message, TelegramLongPollingBot bot) {
        this.message = message;
        this.bot = bot;
    }

    @ExceptionMapping
    public void handleException(Update update, Exception e) {
        long chatId = update.getMessage().getChatId();
        SendMessage messageMethod = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            bot.execute(messageMethod);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }
}
