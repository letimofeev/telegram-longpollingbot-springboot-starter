package org.telegram.telegrambot.dto;

import java.lang.reflect.Method;

public class MethodTargetPair {

    private final Method method;
    private final Object target;

    public MethodTargetPair(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public Object getTarget() {
        return target;
    }
}
