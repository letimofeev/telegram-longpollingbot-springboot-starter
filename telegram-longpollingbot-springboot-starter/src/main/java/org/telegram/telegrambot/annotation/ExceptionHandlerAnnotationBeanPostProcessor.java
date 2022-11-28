
package org.telegram.telegrambot.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.validator.UpdateMappingMethodSignatureValidator;

import java.lang.reflect.Method;

@Component
public class ExceptionHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAnnotationBeanPostProcessor.class);

    private final ExceptionMappingMethodContainer methodContainer;
    private final UpdateMappingMethodSignatureValidator methodSignatureValidator;

    public ExceptionHandlerAnnotationBeanPostProcessor(ExceptionMappingMethodContainer methodContainer,
                                                       UpdateMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(ExceptionHandler.class)) {
            for (Method method : beanClass.getDeclaredMethods()) {
                saveExceptionMappingMethod(bean, method);
            }
        }
        return bean;
    }

    private void saveExceptionMappingMethod(Object bean, Method method) {
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
                log.info("Default exception handler overwritten, mapped exception [{}] handling onto {}",
                        exceptionType.getName(), exceptionMapping.getMethod());
            }
        }, () -> {
            methodContainer.put(exceptionType, exceptionMapping);
            log.info("Mapped exception [{}] handling onto {}", exceptionType.getName(), exceptionMapping.getMethod());
        });
    }
}
