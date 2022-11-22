package org.telegram.telegrambot.expection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DefaultExceptionHandler implements ExceptionHandler {

    private final String message;
    private final TelegramLongPollingBot bot;

    public DefaultExceptionHandler(String message, TelegramLongPollingBot bot) {
        this.message = message;
        this.bot = bot;
    }

    @Override
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
