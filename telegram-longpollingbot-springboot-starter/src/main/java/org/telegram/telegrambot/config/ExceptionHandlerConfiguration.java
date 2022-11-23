package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.ExceptionHandlerAnnotationBeanPostProcessor;
import org.telegram.telegrambot.aop.ExceptionHandlerAspect;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.handler.ApiMethodsReturningMethodInvoker;
import org.telegram.telegrambot.container.ExceptionMappingMethodContainer;

@Configuration
public class ExceptionHandlerConfiguration {

    @Bean
    public ExceptionHandlerAnnotationBeanPostProcessor exceptionHandlerAnnotationBeanPostProcessor(ExceptionMappingMethodContainer methodContainer) {
        return new ExceptionHandlerAnnotationBeanPostProcessor(methodContainer);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultExceptionHandler defaultExceptionHandler(@Value("${telegrambot.exception.default-message:Something went wrong...}") String message) {
        return new DefaultExceptionHandler(message);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionMappingMethodContainer exceptionMappingMethodContainer() {
        return new ExceptionMappingMethodContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerAspect exceptionHandlerAspect(ExceptionMappingMethodContainer exceptionMappingMethodContainer,
                                                         ApiMethodsReturningMethodInvoker exceptionMappingMethodInvoker) {
        return new ExceptionHandlerAspect(exceptionMappingMethodContainer, exceptionMappingMethodInvoker);
    }
}
