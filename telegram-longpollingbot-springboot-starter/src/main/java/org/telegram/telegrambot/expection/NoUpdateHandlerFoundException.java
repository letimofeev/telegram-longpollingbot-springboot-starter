package org.telegram.telegrambot.expection;

public class NoUpdateHandlerFoundException extends RuntimeException {

    public NoUpdateHandlerFoundException() {
    }

    public NoUpdateHandlerFoundException(String message) {
        super(message);
    }

    public NoUpdateHandlerFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
