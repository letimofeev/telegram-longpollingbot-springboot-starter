package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambot.aop.ExceptionHandlerAspect;
import org.telegram.telegrambot.expection.DefaultExceptionHandler;
import org.telegram.telegrambot.expection.ExceptionHandler;
import org.telegram.telegrambot.model.ExceptionHandlerContainer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.List;

@Configuration
public class ExceptionHandlerConfiguration {

    @Bean
    @ConditionalOnProperty("telegrambot.exception.default-message")
    @ConditionalOnMissingBean
    public ExceptionHandler defaultExceptionHandlerWithMessage(@Value("${telegrambot.exception.default-message}") String message, TelegramLongPollingBot bot) {
        return new DefaultExceptionHandler(message, bot);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandler defaultExceptionHandlerWithDefaultMessage(TelegramLongPollingBot bot) {
        return new DefaultExceptionHandler("Something went wrong...", bot);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerContainer exceptionHandlerContainer() {
        return new ExceptionHandlerContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerAspect exceptionHandlerAspect(ExceptionHandlerContainer exceptionHandlerContainer) {
        return new ExceptionHandlerAspect(exceptionHandlerContainer);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerContainerInitializer handlerContainerInitializer(List<ExceptionHandler> handlers, ExceptionHandlerContainer exceptionHandlerContainer) {
        return new ExceptionHandlerContainerInitializer(handlers, exceptionHandlerContainer);
    }
}
