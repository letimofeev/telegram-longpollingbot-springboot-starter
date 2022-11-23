package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.ExceptionHandlerAnnotationBeanPostProcessor;
import org.telegram.telegrambot.aop.ExceptionHandlerAspect;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.expection.handler.ExceptionMappingMethodInvoker;
import org.telegram.telegrambot.expection.handler.SendingExceptionMappingMethodInvoker;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.model.ExceptionMappingMethodContainer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
public class ExceptionHandlerConfiguration {

    @Bean
    public ExceptionHandlerAnnotationBeanPostProcessor exceptionHandlerAnnotationBeanPostProcessor(ExceptionMappingMethodContainer methodContainer) {
        return new ExceptionHandlerAnnotationBeanPostProcessor(methodContainer);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultExceptionHandler defaultExceptionHandler(@Value("${telegrambot.exception.default-message:Something went wrong...}") String message,
                                                           TelegramLongPollingBot bot) {
        return new DefaultExceptionHandler(message, bot);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionMappingMethodContainer exceptionMappingMethodContainer() {
        return new ExceptionMappingMethodContainer();
    }

    @Bean
    public ExceptionMappingMethodInvoker exceptionMappingMethodInvoker(TelegramLongPollingBot bot,
                                                                       BotApiMethodExecutorResolver botApiMethodExecutorResolver) {
        return new SendingExceptionMappingMethodInvoker(bot, botApiMethodExecutorResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerAspect exceptionHandlerAspect(ExceptionMappingMethodContainer exceptionMappingMethodContainer,
                                                         ExceptionMappingMethodInvoker exceptionMappingMethodInvoker) {
        return new ExceptionHandlerAspect(exceptionMappingMethodContainer, exceptionMappingMethodInvoker);
    }
}