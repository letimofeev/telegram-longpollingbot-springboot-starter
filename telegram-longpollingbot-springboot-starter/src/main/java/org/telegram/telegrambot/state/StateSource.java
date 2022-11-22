package org.telegram.telegrambot.state;

public interface StateSource {

    void setState(long chatId, String state);

    String getState(long chatId);
}
