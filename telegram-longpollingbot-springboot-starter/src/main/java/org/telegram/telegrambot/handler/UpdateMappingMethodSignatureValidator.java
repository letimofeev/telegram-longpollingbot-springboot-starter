package org.telegram.telegrambot.handler;

import java.lang.reflect.Method;

public interface UpdateMappingMethodSignatureValidator {

    void validateMethodSignature(Method method);
}
