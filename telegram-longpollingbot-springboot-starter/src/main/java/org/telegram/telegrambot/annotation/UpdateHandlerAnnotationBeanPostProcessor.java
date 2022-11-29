package org.telegram.telegrambot.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.MessageMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.validator.MessageMappingMethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Logger log = LoggerFactory.getLogger(UpdateHandlerAnnotationBeanPostProcessor.class);

    private final MessageMappingMethodContainer methodContainer;
    private final MessageMappingMethodSignatureValidator methodSignatureValidator;

    public UpdateHandlerAnnotationBeanPostProcessor(MessageMappingMethodContainer methodContainer,
                                                    MessageMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(UpdateHandler.class)) {
            for (Method method : beanClass.getDeclaredMethods()) {
                saveMessageMappingMethod(bean, method);
            }
        }
        return bean;
    }

    private void saveMessageMappingMethod(Object bean, Method method) {
        MessageMapping annotation = method.getAnnotation(MessageMapping.class);
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
        methodContainer.get(state).ifPresent(mappingMethod -> validateDuplicates(messageRegex, method, mappingMethod));
    }

    private void validateDuplicates(String messageRegex, Method method, List<MethodTargetPair> mappingMethod) {
        for (MethodTargetPair methodTargetPair : mappingMethod) {
            String storedMethodMessageRegex = getMethodMessageRegex(methodTargetPair);
            if (storedMethodMessageRegex.equals(messageRegex)) {
                String message = String.format("Found duplicate method annotated as @MessageMapping with " +
                        "same state and messageRegex: %s and %s", methodTargetPair.getMethod(), method);
                throw new IllegalStateException(message);
            }
        }
    }

    private String getMethodMessageRegex(MethodTargetPair methodTargetPair) {
        Method storedMethod = methodTargetPair.getMethod();
        MessageMapping annotation = storedMethod.getAnnotation(MessageMapping.class);
        return annotation.messageRegex();
    }
}
