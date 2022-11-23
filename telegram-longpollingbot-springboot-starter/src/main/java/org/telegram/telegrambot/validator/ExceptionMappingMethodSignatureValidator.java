package org.telegram.telegrambot.validator;

import org.telegram.telegrambot.expection.MethodSignatureValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

public class ExceptionMappingMethodSignatureValidator extends AbstractMethodSignatureValidator {

    private static final int REQUIRED_PARAMETERS_NUMBER = 2;
    private static final Class<?>[] REQUIRED_PARAMETERS_TYPES = {Update.class, Exception.class};
    private static final Class<?>[] ALLOWED_RETURN_TYPES = {PartialBotApiMethod.class, void.class};

    @Override
    public void validateMethodSignature(Method method) {
        try {
            validateParametersNumber(method, REQUIRED_PARAMETERS_NUMBER);
            validateParametersTypes(method, REQUIRED_PARAMETERS_TYPES);
            validateReturnType(method, ALLOWED_RETURN_TYPES);
        } catch (Exception e) {
            String message = String.format("Exception during validating @ExceptionMapping method %s, nested exception: %s", method, e);
            throw new MethodSignatureValidationException(message, e);
        }
    }
}
