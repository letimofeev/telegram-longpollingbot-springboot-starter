package org.telegram.telegrambot.config;

import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambot.expection.ExceptionHandler;
import org.telegram.telegrambot.model.ExceptionHandlerContainer;

import java.lang.reflect.Method;
import java.util.List;

public class ExceptionHandlerContainerInitializer implements InitializingBean {

    private final List<ExceptionHandler> exceptionHandlers;
    private final ExceptionHandlerContainer exceptionHandlerContainer;

    public ExceptionHandlerContainerInitializer(List<ExceptionHandler> exceptionHandlers, ExceptionHandlerContainer exceptionHandlerContainer) {
        this.exceptionHandlers = exceptionHandlers;
        this.exceptionHandlerContainer = exceptionHandlerContainer;
    }

    @Override
    public void afterPropertiesSet() {
        for (ExceptionHandler exceptionHandler : exceptionHandlers) {
            for (Method method : exceptionHandler.getClass().getDeclaredMethods()) {
                ExceptionMapping annotation = method.getAnnotation(ExceptionMapping.class);
                if (annotation != null) {
                    Class<? extends Exception> exceptionType = annotation.value();
                    exceptionHandlerContainer.putExceptionHandler(exceptionType, exceptionHandler);
                }
            }
        }
    }
}
