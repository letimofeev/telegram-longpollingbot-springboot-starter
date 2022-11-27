package org.telegram.telegrambot.converter;

import org.springframework.stereotype.Component;

@Component
public class StringToStringObjectMapper implements StringToObjectMapper<String> {

    @Override
    public String mapToObject(String value) {
        return value;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
