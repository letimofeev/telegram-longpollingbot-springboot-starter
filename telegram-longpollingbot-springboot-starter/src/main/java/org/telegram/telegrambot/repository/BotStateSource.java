package org.telegram.telegrambot.repository;


public interface BotStateSource {

    void setState(long chatId, String state);

    String getState(long chatId);
}
