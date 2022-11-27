package org.telegram.telegrambot.dto;

import java.lang.reflect.Method;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodTargetPair that = (MethodTargetPair) o;

        if (!Objects.equals(method, that.method)) return false;
        return Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodTargetPair{" +
                "method=" + method +
                ", target=" + target +
                '}';
    }
}
