package org.telegram.telegrambot.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.telegram.telegrambot.validator.UpdateMappingMethodSignatureValidator;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambot.model.UpdateMappingMethodContainer;

import java.lang.reflect.Method;
import java.util.Optional;

public class UpdateMappingAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final UpdateMappingMethodContainer methodContainer;
    private final UpdateMappingMethodSignatureValidator methodSignatureValidator;

    public UpdateMappingAnnotationBeanPostProcessor(UpdateMappingMethodContainer methodContainer, UpdateMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(UpdateHandler.class)) {
            collectAllUpdateMappings(bean, beanClass);
        }
        return bean;
    }

    private void collectAllUpdateMappings(Object bean, Class<?> beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            UpdateMapping annotation = method.getAnnotation(UpdateMapping.class);
            if (annotation != null) {
                methodSignatureValidator.validateMethodSignature(method);
                String state = annotation.state();
                validateDuplicates(state, method);
                methodContainer.putMappingMethod(state, new MethodTargetPair(method, bean));
            }
        }
    }

    private void validateDuplicates(String state, Method method) {
        Optional<MethodTargetPair> mappingMethodOptional = methodContainer.getMappingMethod(state);
        if (mappingMethodOptional.isPresent()) {
            Method storedMethod = mappingMethodOptional.get().getMethod();
            String message = String.format("Found duplicate method annotated as @UpdateMapping with same state: %s and %s", storedMethod, method);
            throw new IllegalStateException(message);
        }
    }
}
