package org.telegram.telegrambot.expection;

public class BotApiMethodExecutorResolverException extends RuntimeException {

    public BotApiMethodExecutorResolverException() {
    }

    public BotApiMethodExecutorResolverException(String message) {
        super(message);
    }

    public BotApiMethodExecutorResolverException(String message, Throwable cause) {
        super(message, cause);
    }
}
