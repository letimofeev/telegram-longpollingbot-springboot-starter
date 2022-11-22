package org.telegram.telegrambot.expection;

public class UpdateMappingMethodValidationException extends RuntimeException {

    public UpdateMappingMethodValidationException() {
    }

    public UpdateMappingMethodValidationException(String message) {
        super(message);
    }

    public UpdateMappingMethodValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
