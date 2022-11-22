package org.telegram.telegrambot.model;

import java.lang.reflect.Method;

public class UpdateMappingMethod {

    private final Method method;
    private final Object handler;

    public UpdateMappingMethod(Method method, Object handler) {
        this.method = method;
        this.handler = handler;
    }

    public Method getMethod() {
        return method;
    }

    public Object getHandler() {
        return handler;
    }
}
