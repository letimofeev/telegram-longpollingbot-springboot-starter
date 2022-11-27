package org.telegram.telegrambot.container.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.ExceptionHandler;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambot.container.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.validator.ExceptionMappingMethodSignatureValidator;
import org.telegram.telegrambot.validator.MethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.List;

@Component
public class ExceptionMappingMethodContainerInitializer extends AbstractContainerInitializer<Class<? extends Exception>, MethodTargetPair> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionMappingMethodContainerInitializer.class);

    private final MethodSignatureValidator methodSignatureValidator;

    @Autowired
    @ExceptionHandler
    private List<Object> handlers;

    public ExceptionMappingMethodContainerInitializer(ExceptionMappingMethodContainer methodContainer,
                                                      ExceptionMappingMethodSignatureValidator methodSignatureValidator) {
        super(methodContainer);
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    protected void processBean(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            processBeanMethod(bean, method);
        }
    }

    @Override
    protected List<Object> getBeans() {
        return handlers;
    }

    @Override
    protected void postProcessSavedObject(Class<? extends Exception> key, MethodTargetPair methodTargetPair) {
        log.info("Mapped exception [{}] handling onto {}", key, methodTargetPair.getMethod());
    }

    private void processBeanMethod(Object bean, Method method) {
        ExceptionMapping annotation = method.getAnnotation(ExceptionMapping.class);
        if (annotation != null) {
            Class<? extends Exception> exceptionType = annotation.value();
            methodSignatureValidator.validateMethodSignature(method);
            validateDuplicates(exceptionType, method, bean);
            saveWithDefaultExceptionHandlerOverwriting(bean, method, exceptionType);
        }
    }

    private void validateDuplicates(Class<? extends Exception> exceptionType, Method method, Object bean) {
        methodContainer.get(exceptionType).ifPresent(exceptionMapping ->
                checkIfDuplicateNotDefaultExceptionHandler(exceptionType, method, bean, exceptionMapping));
    }

    private void checkIfDuplicateNotDefaultExceptionHandler(Class<? extends Exception> exceptionType, Method method,
                                                            Object bean, MethodTargetPair storedExceptionMapping) {
        Object storedTarget = storedExceptionMapping.getTarget();
        if (!(storedTarget instanceof DefaultExceptionHandler || bean instanceof DefaultExceptionHandler)) {
            Method storedMethod = storedExceptionMapping.getMethod();
            String message = String.format("Found duplicate method annotated as @ExceptionMapping with same exception %s: " +
                    "%s and %s", exceptionType.getName(), storedMethod, method);
            throw new IllegalStateException(message);
        }
    }

    private void saveWithDefaultExceptionHandlerOverwriting(Object bean, Method method, Class<? extends Exception> exceptionType) {
        MethodTargetPair exceptionMapping = new MethodTargetPair(method, bean);
        methodContainer.get(exceptionType).ifPresentOrElse(storedExceptionMapping -> {
            if (storedExceptionMapping.getTarget() instanceof DefaultExceptionHandler) {
                methodContainer.put(exceptionType, exceptionMapping);
            }
        }, () -> methodContainer.put(exceptionType, exceptionMapping));
    }
}

