package org.telegram.telegrambot.converter;

public interface StringToObjectMapper<T> {

    T mapToObject(String value);

    Class<T> getType();
}
