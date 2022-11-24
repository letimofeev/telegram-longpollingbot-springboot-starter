package org.telegram.telegrambot.container.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.UpdateHandler;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.container.MethodTargetPairContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.validator.UpdateMappingMethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateMappingMethodContainerInitializer extends MethodTargetPairContainerInitializer<String> {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodContainerInitializer.class);

    @Autowired
    @UpdateHandler
    private List<Object> handlers;

    protected UpdateMappingMethodContainerInitializer(MethodTargetPairContainer<String> methodContainer,
                                                      UpdateMappingMethodSignatureValidator methodSignatureValidator) {
        super(methodContainer, methodSignatureValidator);
    }

    @Override
    protected void processBean(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            UpdateMapping annotation = method.getAnnotation(UpdateMapping.class);
            if (annotation != null) {
                methodSignatureValidator.validateMethodSignature(method);
                String state = annotation.state();
                validateDuplicates(state, method);
                String stateLowerCase = state.toLowerCase();
                methodContainer.putMethodTargetPair(stateLowerCase, new MethodTargetPair(method, bean));
            }
        }
    }

    @Override
    protected List<Object> getBeans() {
        return handlers;
    }

    @Override
    protected void processSavedMethodTargetPair(String key, MethodTargetPair methodTargetPair) {
        log.info("Mapped state [\"{}\"] onto {}", key, methodTargetPair.getMethod());
    }

    private void validateDuplicates(String state, Method method) {
        String stateLowerCase = state.toLowerCase();
        Optional<MethodTargetPair> mappingMethodOptional = methodContainer.getMethodTargetPair(stateLowerCase);
        if (mappingMethodOptional.isPresent()) {
            Method storedMethod = mappingMethodOptional.get().getMethod();
            String message = String.format("Found duplicate method annotated as @UpdateMapping with same state: " +
                    "%s and %s", storedMethod, method.getName());
            throw new IllegalStateException(message);
        }
    }
}
