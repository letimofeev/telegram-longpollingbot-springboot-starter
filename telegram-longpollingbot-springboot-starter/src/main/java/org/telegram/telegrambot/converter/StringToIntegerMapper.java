package org.telegram.telegrambot.converter;

import org.springframework.stereotype.Component;

@Component
public class StringToIntegerMapper implements StringToObjectMapper<Integer> {

    @Override
    public Integer mapToObject(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
