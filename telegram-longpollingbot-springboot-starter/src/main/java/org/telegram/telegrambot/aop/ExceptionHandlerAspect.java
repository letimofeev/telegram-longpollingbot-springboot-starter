package org.telegram.telegrambot.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.expection.handler.ExceptionMappingMethodInvoker;
import org.telegram.telegrambot.model.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
public class ExceptionHandlerAspect {

    private final ExceptionMappingMethodContainer exceptionMappingMethodContainer;
    private final ExceptionMappingMethodInvoker exceptionMappingMethodInvoker;

    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAspect.class);

    public ExceptionHandlerAspect(ExceptionMappingMethodContainer exceptionMappingMethodContainer,
                                  ExceptionMappingMethodInvoker exceptionMappingMethodInvoker) {
        this.exceptionMappingMethodContainer = exceptionMappingMethodContainer;
        this.exceptionMappingMethodInvoker = exceptionMappingMethodInvoker;
    }

    @Around("execution(public void org.telegram.telegrambot.handler.UpdateDispatcher.executeHandlerOnUpdate(..))")
    public Object aroundExecuteHandlerOnUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            handleException(joinPoint, e);
        }
        return null;
    }

    private void handleException(ProceedingJoinPoint joinPoint, Exception e) throws Exception {
        log.warn("Exception during onUpdateReceive() method, nested exception: {}", e.toString());
        Optional<MethodTargetPair> optional = exceptionMappingMethodContainer.getExceptionMapping(e.getClass());
        if (optional.isPresent()) {
            MethodTargetPair methodTargetPair = optional.get();
            Update update = (Update) joinPoint.getArgs()[0];
            log.warn("Using ExceptionMapping method: {} of class {}", methodTargetPair.getMethod().toString(),
                    methodTargetPair.getTarget().getClass().toString());
            exceptionMappingMethodInvoker.invokeExceptionMappingMethod(methodTargetPair, update, e);
        } else {
            log.error("No handlers found for exception {}", e.getClass());
            throw e;
        }
    }
}
