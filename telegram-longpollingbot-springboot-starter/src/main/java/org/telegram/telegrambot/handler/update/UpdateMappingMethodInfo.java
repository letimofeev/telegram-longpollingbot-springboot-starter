package org.telegram.telegrambot.handler.update;

import java.lang.reflect.Method;

public class UpdateMappingMethodInfo {

    private final Object target;
    private final Method method;

    public UpdateMappingMethodInfo(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }
}
