package org.telegram.telegrambot.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.expection.MethodSignatureValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Collection;

@Component
public class ExceptionMappingMethodSignatureValidator extends AbstractMethodSignatureValidator {

    private static final Logger log = LoggerFactory.getLogger(ExceptionMappingMethodSignatureValidator.class);

    private static final int REQUIRED_PARAMETERS_NUMBER = 2;
    private static final Class<?>[] REQUIRED_PARAMETERS_TYPES = {Update.class, Exception.class};
    private static final Class<?>[] ALLOWED_RETURN_TYPES = {PartialBotApiMethod.class, Collection.class};

    @Override
    public void validateMethodSignature(Method method) {
        log.debug("Validating exception handler method: {}", method);

        try {
            validateParametersNumber(method, REQUIRED_PARAMETERS_NUMBER);
            validateReturnType(method, ALLOWED_RETURN_TYPES);
            validateParametersTypes(method, REQUIRED_PARAMETERS_TYPES);
        } catch (Exception e) {
            String message = String.format("Exception during validating @ExceptionMapping method %s, nested exception: %s", method, e);
            throw new MethodSignatureValidationException(message, e);
        }
        log.trace("Exception handler method {} passed validation", method);
    }
}
