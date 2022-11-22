package org.telegram.telegrambot.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.model.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
public class ExceptionHandlerAspect {

    private final ExceptionMappingMethodContainer exceptionMappingMethodContainer;

    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAspect.class);

    public ExceptionHandlerAspect(ExceptionMappingMethodContainer exceptionMappingMethodContainer) {
        this.exceptionMappingMethodContainer = exceptionMappingMethodContainer;
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
        log.warn("Exception during onUpdateReceive() method, nested exception: {}", e.toString());
        Optional<MethodTargetPair> optional = exceptionMappingMethodContainer.getExceptionMapping(e.getClass());
        if (optional.isPresent()) {
            MethodTargetPair methodTargetPair = optional.get();
            Method method = methodTargetPair.getMethod();
            Object target = methodTargetPair.getTarget();
            log.warn("Using ExceptionMapping method: {}", method.toString());
            Update update = (Update) joinPoint.getArgs()[0];
            ReflectionUtils.invokeMethod(method, target, update, e);
            return null;
        }
        throw e;
    }
}
