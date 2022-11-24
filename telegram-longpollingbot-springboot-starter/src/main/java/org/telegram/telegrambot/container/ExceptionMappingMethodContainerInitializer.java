package org.telegram.telegrambot.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.ExceptionHandler;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.validator.ExceptionMappingMethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Component
public class ExceptionMappingMethodContainerInitializer implements InitializingBean {

    @Autowired
    @ExceptionHandler
    private List<Object> handlers;
    
    private final ExceptionMappingMethodContainer methodContainer;
    private final ExceptionMappingMethodSignatureValidator methodSignatureValidator;

    private final Logger log = LoggerFactory.getLogger(ExceptionMappingMethodContainerInitializer.class);

    public ExceptionMappingMethodContainerInitializer(ExceptionMappingMethodContainer methodContainer,
                                                      ExceptionMappingMethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public void afterPropertiesSet() {
        for (Object handler : handlers) {
            System.out.println(handler.getClass());
            collectAllHandlerMappings(handler);
        }
    }

    private void collectAllHandlerMappings(Object handler) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ExceptionMapping annotation = method.getAnnotation(ExceptionMapping.class);
            if (annotation != null) {
                Class<? extends Exception> exceptionType = annotation.value();
                methodSignatureValidator.validateMethodSignature(method);
                validateDuplicates(exceptionType, method, handler);
            }
        }
    }

    private void validateDuplicates(Class<? extends Exception> exceptionType, Method method, Object bean) {
        Optional<MethodTargetPair> exceptionMappingOptional = methodContainer.getExactExceptionMapping(exceptionType);
        if (exceptionMappingOptional.isPresent()) {
            MethodTargetPair storedExceptionMapping = exceptionMappingOptional.get();
            Object storedTarget = exceptionMappingOptional.get().getTarget();
            if (!(storedTarget instanceof DefaultExceptionHandler) && !(bean instanceof DefaultExceptionHandler)) {
                Method storedMethod = storedExceptionMapping.getMethod();
                String message = String.format("Found duplicate method annotated as @ExceptionMapping with same exception %s: " +
                        "%s and %s", exceptionType.getName(), storedMethod, method);
                throw new IllegalStateException(message);
            } else {
                return;
            }
        }
        methodContainer.putExceptionMapping(exceptionType, new MethodTargetPair(method, bean));
        log.info("Mapped exception {} handling onto {}", exceptionType.getName(), method);
    }
}

