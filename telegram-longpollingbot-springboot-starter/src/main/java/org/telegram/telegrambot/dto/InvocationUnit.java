package org.telegram.telegrambot.dto;

import java.lang.reflect.Method;

public class InvocationUnit {

    private final MethodTargetPair methodTargetPair;
    private final Object[] args;

    public InvocationUnit(MethodTargetPair methodTargetPair, Object[] args) {
        this.methodTargetPair = methodTargetPair;
        this.args = args;
    }

    public Method getMethod() {
        return methodTargetPair.getMethod();
    }

    public Object getTarget() {
        return methodTargetPair.getTarget();
    }

    public Object[] getArgs() {
        return args;
    }
}
