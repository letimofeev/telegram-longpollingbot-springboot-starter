package org.telegram.telegrambot.expection;

public class MethodSignatureValidationException extends RuntimeException {

    public MethodSignatureValidationException() {
    }

    public MethodSignatureValidationException(String message) {
        super(message);
    }

    public MethodSignatureValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
