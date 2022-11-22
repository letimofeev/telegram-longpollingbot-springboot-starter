package org.telegram.telegrambot.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.telegram.telegrambot.model.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambot.validator.ExceptionMappingMethodSignatureValidator;

import java.lang.reflect.Method;

public class ExceptionHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ExceptionMappingMethodContainer methodContainer;
    private final ExceptionMappingMethodSignatureValidator methodSignatureValidator;

    public ExceptionHandlerAnnotationBeanPostProcessor(ExceptionMappingMethodContainer methodContainer, ExceptionMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ExceptionMapping annotation = method.getAnnotation(ExceptionMapping.class);
            if (annotation != null) {
                Class<? extends Exception> exceptionType = annotation.value();
                methodSignatureValidator.validateMethodSignature(method);
                methodContainer.putExceptionMapping(exceptionType, new MethodTargetPair(method, bean));
            }
        }
        return bean;
    }
}
