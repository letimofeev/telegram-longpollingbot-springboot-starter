package org.telegram.telegrambot.repository;


public interface StateSource {

    void setState(long chatId, String state);

    String getState(long chatId);
}
