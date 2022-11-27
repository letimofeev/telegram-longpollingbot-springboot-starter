package org.telegram.telegrambot.basic.handler;

import org.telegram.telegrambot.annotation.UpdateHandler;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@UpdateHandler
public class BotGreetingHandler {

    @UpdateMapping
    public SendMessage greeting(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String username = message.getChat().getUserName();
        String greeting = "Hello, " + username + "!";
        return SendMessage.builder()
                .chatId(chatId)
                .text(greeting)
                .build();
    }

}
