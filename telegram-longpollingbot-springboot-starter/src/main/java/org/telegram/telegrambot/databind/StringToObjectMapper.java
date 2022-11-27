package org.telegram.telegrambot.databind;

public interface StringToObjectMapper<T> {

    T mapToObject(String value);

    Class<T> getType();
}
