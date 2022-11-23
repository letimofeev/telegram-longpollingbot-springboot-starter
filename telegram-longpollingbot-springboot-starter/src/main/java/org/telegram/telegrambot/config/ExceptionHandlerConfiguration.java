package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.ExceptionHandlerAnnotationBeanPostProcessor;
import org.telegram.telegrambot.aop.ExceptionHandlerAspect;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.model.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.validator.ExceptionMappingMethodSignatureValidator;
import org.telegram.telegrambot.validator.LongBollingBotExceptionMappingMethodSignatureValidator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
public class ExceptionHandlerConfiguration {

    @Bean
    public ExceptionMappingMethodSignatureValidator exceptionMappingMethodSignatureValidator() {
        return new LongBollingBotExceptionMappingMethodSignatureValidator();
    }

    @Bean
    public ExceptionHandlerAnnotationBeanPostProcessor exceptionHandlerAnnotationBeanPostProcessor(ExceptionMappingMethodContainer methodContainer,
                                                                                                   ExceptionMappingMethodSignatureValidator methodSignatureValidator) {
        return new ExceptionHandlerAnnotationBeanPostProcessor(methodContainer, methodSignatureValidator);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultExceptionHandler defaultExceptionHandler(@Value("${telegrambot.exception.default-message:Something went wrong...}") String message,
                                                           TelegramLongPollingBot bot) {
        return new DefaultExceptionHandler(message, bot);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionMappingMethodContainer exceptionHandlerContainer() {
        return new ExceptionMappingMethodContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerAspect exceptionHandlerAspect(ExceptionMappingMethodContainer exceptionMappingMethodContainer) {
        return new ExceptionHandlerAspect(exceptionMappingMethodContainer);
    }
}
