package org.telegram.telegrambot.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBotStateSource implements BotStateSource {

    private final Map<Long, String> states = new ConcurrentHashMap<>();
    private final String initialState;

    public DefaultBotStateSource(String initialState) {
        this.initialState = initialState;
    }

    @Override
    public void setState(long chatId, String state) {
        states.put(chatId, state.toLowerCase());
    }

    @Override
    public String getState(long chatId) {
        states.putIfAbsent(chatId, initialState);
        return states.get(chatId);
    }
}
