package org.telegram.telegrambot.expection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambot.annotation.ExceptionHandler;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExceptionHandler
public class DefaultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    private final String message;

    public DefaultExceptionHandler(@Value("${telegrambot.exception.default-message:Something went wrong...}") String message) {
        this.message = message;
    }

    @ExceptionMapping
    public SendMessage handleException(Update update, Exception e) {
        log.warn("Caught exception [{}] by default exception handler", e.getClass());
        e.printStackTrace();
        long chatId = update.getMessage().getChatId();
        return SendMessage.builder().chatId(chatId).text(message).build();
    }
}
