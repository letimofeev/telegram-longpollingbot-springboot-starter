package org.telegram.telegrambot.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.expection.MethodSignatureValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

@Component
public class UpdateMappingMethodSignatureValidator extends AbstractMethodSignatureValidator {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodSignatureValidator.class);

    private static final Class<?> REQUIRED_FIRST_PARAMETER_TYPE = Update.class;
    private static final Class<?>[] ALLOWED_RETURN_TYPES = {PartialBotApiMethod.class, Collection.class};

    @Override
    public void validateMethodSignature(Method method) {
        log.debug("Validating update handler method: {}", method);
        try {
            validateFirstParameterType(method);
            validateReturnType(method, ALLOWED_RETURN_TYPES);
        } catch (Exception e) {
            String message = String.format("Exception during validating @UpdateMapping method %s, nested exception: %s", method.getName(), e);
            throw new MethodSignatureValidationException(message, e);
        }
        log.trace("Update handler method {} passed validation", method);
    }

    protected void validateFirstParameterType(Method method) {
        log.trace("Validation method {} first parameter type, expected instanse of class: {}",
                method, REQUIRED_FIRST_PARAMETER_TYPE.getName());
        Parameter[] parameters = method.getParameters();
        if (method.getParameterCount() < 1) {
            String message = String.format("Unresolved parameters count for annotated method %s, " +
                    "expected at least 1 parameter instance of type: %s", method, Update.class);
            throw new MethodSignatureValidationException(message);
        }
        Class<?> parameterType = parameters[0].getType();
        if (!REQUIRED_FIRST_PARAMETER_TYPE.isAssignableFrom(parameterType)) {
            String message = String.format("Unresolved parameter for annotated method %s, " +
                    "expected that first parameter is instance of: %s", method, Update.class);
            throw new MethodSignatureValidationException(message);
        }
    }
}
