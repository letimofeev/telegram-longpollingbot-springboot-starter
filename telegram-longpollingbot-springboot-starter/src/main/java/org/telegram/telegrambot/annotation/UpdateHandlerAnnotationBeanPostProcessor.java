package org.telegram.telegrambot.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.UpdateMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.validator.UpdateMappingMethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Logger log = LoggerFactory.getLogger(UpdateHandlerAnnotationBeanPostProcessor.class);

    private final UpdateMappingMethodContainer methodContainer;
    private final UpdateMappingMethodSignatureValidator methodSignatureValidator;

    public UpdateHandlerAnnotationBeanPostProcessor(UpdateMappingMethodContainer methodContainer,
                                                    UpdateMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(UpdateHandler.class)) {
            for (Method method : beanClass.getDeclaredMethods()) {
                saveUpdateMappingMethod(bean, method);
            }
        }
        return bean;
    }

    private void saveUpdateMappingMethod(Object bean, Method method) {
        UpdateMapping annotation = method.getAnnotation(UpdateMapping.class);
        if (annotation != null) {
            methodSignatureValidator.validateMethodSignature(method);
            String state = annotation.state().toLowerCase();
            String messageRegex = annotation.messageRegex();
            validateDuplicates(state, messageRegex, method);
            methodContainer.computeIfAbsent(state, key -> new ArrayList<>()).add(new MethodTargetPair(method, bean));
            log.info("Mapped state [\"{}\"] with message regex [\"{}\"] onto {}", state, messageRegex, method);
        }
    }

    private void validateDuplicates(String state, String messageRegex, Method method) {
        Optional<List<MethodTargetPair>> mappingMethodOptional = methodContainer.get(state);
        mappingMethodOptional.ifPresent(mappingMethod -> validateDuplicates(messageRegex, method, mappingMethod));
    }

    private void validateDuplicates(String messageRegex, Method method, List<MethodTargetPair> mappingMethod) {
        for (MethodTargetPair methodTargetPair : mappingMethod) {
            String storedMethodMessageRegex = getMethodMessageRegex(methodTargetPair);
            if (storedMethodMessageRegex.equals(messageRegex)) {
                String message = String.format("Found duplicate method annotated as @UpdateMapping with " +
                        "same state and messageRegex: %s and %s", methodTargetPair.getMethod(), method.getName());
                throw new IllegalStateException(message);
            }
        }
    }

    private String getMethodMessageRegex(MethodTargetPair methodTargetPair) {
        Method storedMethod = methodTargetPair.getMethod();
        UpdateMapping annotation = storedMethod.getAnnotation(UpdateMapping.class);
        return annotation.messageRegex();
    }
}
