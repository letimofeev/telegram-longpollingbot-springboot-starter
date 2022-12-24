package org.telegram.telegrambot.handler.update;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

public class InvocableUpdateMappingMethod {

    private final UpdateMappingMethodInfo mappingMethod;
    private final Object[] args;

    public InvocableUpdateMappingMethod(UpdateMappingMethodInfo mappingMethod, Object[] args) {
        this.mappingMethod = mappingMethod;
        this.args = args;
    }

    public Object invokeMethod() {
        Method method = mappingMethod.getMethod();
        Object target = mappingMethod.getTarget();
        return ReflectionUtils.invokeMethod(method, target, args);
    }

    public UpdateMappingMethodInfo getMappingMethod() {
        Method method = mappingMethod.getMethod();
        Object target = mappingMethod.getTarget();
        return new UpdateMappingMethodInfo(target, method);
    }

    public Object[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }
}
