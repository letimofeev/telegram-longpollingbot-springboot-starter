package org.telegram.telegrambot.handler.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.MessageMapping;
import org.telegram.telegrambot.annotation.UpdateHandler;

import java.lang.reflect.Method;

@Component
public class UpdateMappingInfoRegistrar implements BeanPostProcessor {

    private final Logger log = LoggerFactory.getLogger(UpdateMappingInfoRegistrar.class);

    private final UpdateMappingInfoRegistry registry;

    public UpdateMappingInfoRegistrar(UpdateMappingInfoRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(UpdateHandler.class)) {
            for (Method method : beanClass.getDeclaredMethods()) {
                registerMessageMappingMethod(bean, method);
            }
        }
        return bean;
    }

    private void registerMessageMappingMethod(Object bean, Method method) {
        MessageMapping annotation = method.getAnnotation(MessageMapping.class);
        if (annotation != null) {
            UpdateMappingMethodInfo methodInfo = new UpdateMappingMethodInfo(bean, method);
            registry.addMappingInfo(methodInfo);
        }
    }
}
