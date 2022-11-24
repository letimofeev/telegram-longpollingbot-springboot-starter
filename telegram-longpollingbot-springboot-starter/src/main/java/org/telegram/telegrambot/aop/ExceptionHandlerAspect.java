package org.telegram.telegrambot.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.handler.ApiMethodsReturningMethodInvoker;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Aspect
@Component
public class ExceptionHandlerAspect {

    private final ExceptionMappingMethodContainer exceptionMappingMethodContainer;
    private final ApiMethodsReturningMethodInvoker exceptionMappingMethodInvoker;

    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAspect.class);

    public ExceptionHandlerAspect(ExceptionMappingMethodContainer exceptionMappingMethodContainer,
                                  ApiMethodsReturningMethodInvoker exceptionMappingMethodInvoker) {
        this.exceptionMappingMethodContainer = exceptionMappingMethodContainer;
        this.exceptionMappingMethodInvoker = exceptionMappingMethodInvoker;
    }

    @Around("execution(public * org.telegram.telegrambot.handler.UpdateResolver.getResponse(..))")
    public Object aroundOnUpdateReceived(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            return handleException(joinPoint, e);
        }
    }

    private Object handleException(ProceedingJoinPoint joinPoint, Exception e) throws Exception {
        log.warn("Exception during getResponse() method, nested exception: {}", e.toString());
        Optional<MethodTargetPair> optional = exceptionMappingMethodContainer.getExceptionMapping(e.getClass());
        if (optional.isPresent()) {
            MethodTargetPair methodTargetPair = optional.get();
            Update update = (Update) joinPoint.getArgs()[0];
            log.warn("Using ExceptionMapping method: {} of class {}", methodTargetPair.getMethod().toString(),
                    methodTargetPair.getTarget().getClass().toString());
            return exceptionMappingMethodInvoker.invokeMethod(methodTargetPair, update, e);
        } else {
            log.error("No handlers found for exception {}", e.getClass());
            throw e;
        }
    }
}
