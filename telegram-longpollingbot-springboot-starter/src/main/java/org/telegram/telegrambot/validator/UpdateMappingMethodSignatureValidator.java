package org.telegram.telegrambot.validator;

import org.telegram.telegrambot.expection.MethodSignatureValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Collection;

public class UpdateMappingMethodSignatureValidator extends AbstractMethodSignatureValidator {

    private static final int REQUIRED_PARAMETERS_NUMBER = 1;
    private static final Class<?>[] REQUIRED_PARAMETERS_TYPES = {Update.class};
    private static final Class<?>[] ALLOWED_RETURN_TYPES = {PartialBotApiMethod.class, Collection.class};

    @Override
    public void validateMethodSignature(Method method) {
        try {
            validateParametersNumber(method, REQUIRED_PARAMETERS_NUMBER);
            validateParametersTypes(method, REQUIRED_PARAMETERS_TYPES);
            validateReturnType(method, ALLOWED_RETURN_TYPES);
        } catch (Exception e) {
            String message = String.format("Exception during validating @UpdateMapping method %s, nested exception: %s", method.getName(), e);
            throw new MethodSignatureValidationException(message, e);
        }
    }
}
