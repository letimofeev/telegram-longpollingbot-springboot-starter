package org.telegram.telegrambot.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.expection.MethodSignatureValidationException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

@Component
public class UpdateMappingMethodSignatureValidator extends AbstractMethodSignatureValidator {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodSignatureValidator.class);

    private static final Class<?> REQUIRED_FIRST_PARAMETER_TYPE = Update.class;
    private static final Class<?>[] ALLOWED_RETURN_TYPES = {PartialBotApiMethod.class, Collection.class};
    private static final Class<?> ALLOWED_COLLECTION_GENERIC_RETURN_TYPE = PartialBotApiMethod.class;

    @Override
    public void validateMethodSignature(Method method) {
        log.debug("Validating update handler method: {}", method);

        try {
            validateParameters(method);
            validateReturnType(method, ALLOWED_RETURN_TYPES);
            validateGenericReturnType(method);
        } catch (Exception e) {
            String message = String.format("Exception during validating @UpdateMapping method %s, nested exception: %s", method.getName(), e);
            throw new MethodSignatureValidationException(message, e);
        }
        log.trace("Update handler method {} passed validation", method);
    }

    private void validateGenericReturnType(Method method) {
        log.trace("Validation method {} generic return type", method);

        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            ParameterizedType parameterizedReturnType = (ParameterizedType) method.getGenericReturnType();
            Type actualTypeArgument = parameterizedReturnType.getActualTypeArguments()[0];
            if (actualTypeArgument instanceof ParameterizedType) {
                ParameterizedType parameterizedActualTypeArgument = (ParameterizedType) actualTypeArgument;
                actualTypeArgument = parameterizedActualTypeArgument.getRawType();
            }
            Class<?> actualTypeArgumentClass = (Class<?>) actualTypeArgument;
            if (!ALLOWED_COLLECTION_GENERIC_RETURN_TYPE.isAssignableFrom(actualTypeArgumentClass)) {
                String message = String.format("Unresolved return type %s for annotated method %s, " +
                        "expected that Collection generic type is instance of: %s",
                        parameterizedReturnType, method, ALLOWED_COLLECTION_GENERIC_RETURN_TYPE);
                throw new MethodSignatureValidationException(message);
            }
        }
    }

    private void validateParameters(Method method) {
        log.trace("Validating method {} parameters", method);

        validateFirstParameterType(method);

        for (Parameter parameter : method.getParameters()) {
            Class<?> parameterType = parameter.getType();
            if (!Update.class.isAssignableFrom(parameterType) && !parameter.isAnnotationPresent(RegexGroup.class)) {
                String message = String.format("Unresolved parameter %s for annotated method %s, " +
                        "expected that not Update parameter annotated with @RegexGroup", parameter, method);
                throw new MethodSignatureValidationException(message);
            }
        }

    }

    private void validateFirstParameterType(Method method) {
        log.trace("Validating method {} first parameter type, expected instance of class: {}",
                method, REQUIRED_FIRST_PARAMETER_TYPE.getName());

        Parameter[] parameters = method.getParameters();
        if (method.getParameterCount() < 1) {
            String message = String.format("Unresolved parameters count for annotated method %s, " +
                    "expected at least 1 parameter instance of type: %s", method, Update.class);
            throw new MethodSignatureValidationException(message);
        }
        Parameter firstParameter = parameters[0];
        Class<?> parameterType = firstParameter.getType();
        if (!REQUIRED_FIRST_PARAMETER_TYPE.isAssignableFrom(parameterType)) {
            String message = String.format("Unresolved parameter %s for annotated method %s, " +
                    "expected that first parameter is instance of: %s", firstParameter, method, Update.class);
            throw new MethodSignatureValidationException(message);
        }
    }
}
