package org.telegram.telegrambot.validator;

import java.lang.reflect.Method;

public interface MethodSignatureValidator {

    void validateMethodSignature(Method method);
}
