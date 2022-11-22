package org.telegram.telegrambot.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambot.expection.ExceptionHandler;
import org.telegram.telegrambot.model.ExceptionHandlerContainer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Aspect
public class ExceptionHandlerAspect {

    private final ExceptionHandlerContainer exceptionHandlerContainer;

    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAspect.class);

    public ExceptionHandlerAspect(ExceptionHandlerContainer exceptionHandlerContainer) {
        this.exceptionHandlerContainer = exceptionHandlerContainer;
    }

    @Around("execution(public void org.telegram.telegrambot.handler.UpdateDispatcher.executeHandlerOnUpdate(..))")
    public Object aroundExecuteHandlerOnUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            return handleException(joinPoint, e);
        }
    }

    private Object handleException(ProceedingJoinPoint joinPoint, Exception e) throws Exception {
        log.error("Exception during onUpdateReceive() method, nested exception: {}", e.toString());
        Optional<ExceptionHandler> optional = exceptionHandlerContainer.getExceptionHandler(e.getClass());
        if (optional.isPresent()) {
            ExceptionHandler exceptionHandler = optional.get();
            log.info("Using ExceptionHandler: {}", exceptionHandler.getClass().toString());
            Update update = (Update) joinPoint.getArgs()[0];
            exceptionHandler.handleException(update, e);
            return null;
        }
        throw e;
    }
}
