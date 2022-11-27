package org.telegram.telegrambot.example.messageregex.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.annotation.UpdateHandler;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@UpdateHandler
public class BotUpdateHandler {

    @Autowired
    private StateSource stateSource;

    @UpdateMapping
    public SendMessage greeting(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "Hello!\nEnter your first name and last name in format:\nMy first name - {first_name}, last name - {last_name}";
        stateSource.setState(chatId, "Two");
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    @UpdateMapping(state = "two", messageRegex = "My first name - (.+?), last name - (.+)")
    public SendMessage handleName(Update update, @RegexGroup(1) String firstName, @RegexGroup(2) String lastName) {
        long chatId = update.getMessage().getChatId();
        String text = String.format("Hello, %s %s!", firstName, lastName);
        stateSource.setState(chatId, "three");
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    @UpdateMapping(state = "two")
    public SendMessage handleNameWrongMessageSpecified(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "Please, enter your first name and last name";
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
