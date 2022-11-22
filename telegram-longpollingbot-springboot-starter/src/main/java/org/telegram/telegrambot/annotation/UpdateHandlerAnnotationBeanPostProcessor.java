package org.telegram.telegrambot.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.telegram.telegrambot.handler.UpdateMappingMethodSelector;
import org.telegram.telegrambot.handler.UpdateMappingMethodSignatureValidator;
import org.telegram.telegrambot.model.UpdateMappingMethod;
import org.telegram.telegrambot.repository.UpdateMappingMethodContainer;

import java.lang.reflect.Method;

public class UpdateHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final UpdateMappingMethodContainer methodContainer;
    private final UpdateMappingMethodSelector methodSelector;
    private final UpdateMappingMethodSignatureValidator methodSignatureValidator;

    public UpdateHandlerAnnotationBeanPostProcessor(UpdateMappingMethodContainer methodContainer, UpdateMappingMethodSelector methodSelector, UpdateMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSelector = methodSelector;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            UpdateMapping annotation = method.getAnnotation(UpdateMapping.class);
            if (annotation != null) {
                methodSignatureValidator.validateMethodSignature(method);
                String state = annotation.state();
                methodContainer.putMappingMethod(state, new UpdateMappingMethod(method, bean));
            }
        }
        return bean;
    }
}
