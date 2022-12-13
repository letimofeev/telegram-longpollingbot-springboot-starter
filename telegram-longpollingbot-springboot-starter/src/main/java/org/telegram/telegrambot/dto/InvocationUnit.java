package org.telegram.telegrambot.dto;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvocationUnit that = (InvocationUnit) o;

        if (!Objects.equals(methodTargetPair, that.methodTargetPair))
            return false;
        return Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = methodTargetPair != null ? methodTargetPair.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
