package org.telegram.telegrambot.converter;

import org.springframework.stereotype.Component;

@Component
public class StringToBooleanMapper implements StringToObjectMapper<Boolean> {

    @Override
    public Boolean mapToObject(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
