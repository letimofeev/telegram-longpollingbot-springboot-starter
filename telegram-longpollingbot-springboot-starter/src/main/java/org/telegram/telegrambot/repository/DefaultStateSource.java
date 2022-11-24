package org.telegram.telegrambot.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultStateSource implements StateSource {

    private final Map<Long, String> states = new ConcurrentHashMap<>();

    private final String initialState;

    public DefaultStateSource(@Value("${telegrambot.initial-state:}") String initialState) {
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
