package org.telegram.telegrambot.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultStateSource implements StateSource {

    private final Map<Long, String> states = new ConcurrentHashMap<>();

    private final String initialState;

    public DefaultStateSource(String initialState) {
        this.initialState = initialState;
    }

    @Override
    public void setState(long chatId, String state) {
        states.put(chatId, state);
    }

    @Override
    public String getState(long chatId) {
        states.putIfAbsent(chatId, initialState);
        return states.get(chatId);
    }
}
