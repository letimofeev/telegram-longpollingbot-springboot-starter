package org.telegram.telegrambot.expection;

public class UpdateMappingMethodSignatureValidationException extends RuntimeException {

    public UpdateMappingMethodSignatureValidationException() {
    }

    public UpdateMappingMethodSignatureValidationException(String message) {
        super(message);
    }

    public UpdateMappingMethodSignatureValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
