package org.telegram.telegrambot.container.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.ExceptionHandler;
import org.telegram.telegrambot.annotation.ExceptionMapping;
import org.telegram.telegrambot.container.ExceptionMappingMethodContainer;
import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambot.expection.handler.DefaultExceptionHandler;
import org.telegram.telegrambot.validator.ExceptionMappingMethodSignatureValidator;
import org.telegram.telegrambot.validator.MethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Component
public class ExceptionMappingMethodContainerInitializer extends AbstractContainerInitializer<Class<? extends Exception>, MethodTargetPair> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionMappingMethodContainerInitializer.class);

    private final MethodSignatureValidator methodSignatureValidator;

    @Autowired
    @ExceptionHandler
    private List<Object> handlers;

    public ExceptionMappingMethodContainerInitializer(ExceptionMappingMethodContainer methodContainer,
                                                      ExceptionMappingMethodSignatureValidator methodSignatureValidator) {
        super(methodContainer);
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    protected void processBean(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ExceptionMapping annotation = method.getAnnotation(ExceptionMapping.class);
            if (annotation != null) {
                Class<? extends Exception> exceptionType = annotation.value();
                methodSignatureValidator.validateMethodSignature(method);
                validateDuplicates(exceptionType, method, bean);
            }
        }
    }

    @Override
    protected List<Object> getBeans() {
        return handlers;
    }

    @Override
    protected void postProcessSavedObject(Class<? extends Exception> key, MethodTargetPair methodTargetPair) {
        log.info("Mapped exception [{}] handling onto {}", key, methodTargetPair.getMethod());
    }

    private void validateDuplicates(Class<? extends Exception> exceptionType, Method method, Object bean) {
        Optional<MethodTargetPair> exceptionMappingOptional = methodContainer.get(exceptionType);
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
        methodContainer.put(exceptionType, new MethodTargetPair(method, bean));
    }
}

