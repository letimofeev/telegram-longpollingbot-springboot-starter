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
import org.telegram.telegrambot.validator.ExceptionMappingMethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.Optional;

@Component
public class ExceptionHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ExceptionMappingMethodContainer methodContainer;
    private final ExceptionMappingMethodSignatureValidator methodSignatureValidator;

    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAnnotationBeanPostProcessor.class);

    public ExceptionHandlerAnnotationBeanPostProcessor(ExceptionMappingMethodContainer methodContainer, ExceptionMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(ExceptionHandler.class)) {
            collectAllHandlerMappings(bean, beanClass);
        }
        return bean;
    }

    private void collectAllHandlerMappings(Object bean, Class<?> beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            ExceptionMapping annotation = method.getAnnotation(ExceptionMapping.class);
            if (annotation != null) {
                Class<? extends Exception> exceptionType = annotation.value();
                methodSignatureValidator.validateMethodSignature(method);
                putWithDuplicatesValidation(exceptionType, method, bean);
            }
        }
    }

    private void putWithDuplicatesValidation(Class<? extends Exception> exceptionType, Method method, Object bean) {
        Optional<MethodTargetPair> exceptionMappingOptional = methodContainer.getExceptionMapping(exceptionType);
        if (exceptionMappingOptional.isPresent()) {
            MethodTargetPair storedExceptionMapping = exceptionMappingOptional.get();
            if (!(bean instanceof DefaultExceptionHandler)) {
                Method storedMethod = storedExceptionMapping.getMethod();
                Object storedTarget = storedExceptionMapping.getTarget();
                String message = String.format("Found duplicate method annotated as @ExceptionMapping with same exception: " +
                                "%s in class %s and %s in class %s", storedMethod.getName(), storedTarget.getClass().getName(),
                        method.getName(), bean.getClass().getName());
                throw new IllegalStateException(message);
            }
        }
        methodContainer.putExceptionMapping(exceptionType, new MethodTargetPair(method, bean));
        log.info("Mapped exception {} handling onto {}", exceptionType, method);
    }
}
