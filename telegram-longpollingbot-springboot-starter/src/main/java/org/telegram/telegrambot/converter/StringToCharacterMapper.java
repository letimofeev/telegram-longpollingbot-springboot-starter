package org.telegram.telegrambot.converter;

import org.telegram.telegrambot.expection.StringToObjectMapperException;

public class StringToCharacterMapper implements StringToObjectMapper<Character> {

    @Override
    public Character mapToObject(String value) {
        if (value.length() != 1) {
            throw new StringToObjectMapperException(String.format("Cannot convert string [\"%s\"] to char", value));
        }
        return value.toCharArray()[0];
    }

    @Override
    public Class<Character> getType() {
        return Character.class;
    }
}
