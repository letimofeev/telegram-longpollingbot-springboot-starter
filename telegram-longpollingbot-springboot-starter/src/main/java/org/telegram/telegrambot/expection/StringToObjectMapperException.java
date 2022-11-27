package org.telegram.telegrambot.expection;

public class StringToObjectMapperException extends RuntimeException {

    public StringToObjectMapperException() {
        super();
    }

    public StringToObjectMapperException(String message) {
        super(message);
    }

    public StringToObjectMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
