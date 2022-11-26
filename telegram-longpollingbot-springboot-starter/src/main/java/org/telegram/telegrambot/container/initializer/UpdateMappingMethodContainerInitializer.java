package org.telegram.telegrambot.container.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.UpdateHandler;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.container.AbstractContainer;
import org.telegram.telegrambot.container.UpdateMappingMethodContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.validator.UpdateMappingMethodSignatureValidator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateMappingMethodContainerInitializer extends AbstractContainerInitializer<String, List<MethodTargetPair>> {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodContainerInitializer.class);

    @Autowired
    @UpdateHandler
    private List<Object> handlers;

    protected UpdateMappingMethodContainerInitializer(UpdateMappingMethodContainer methodContainer,
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
                String state = annotation.state().toLowerCase();
                String messageRegex = annotation.messageRegex();
                validateDuplicates(state, messageRegex, method);
                Optional<List<MethodTargetPair>> optional = methodContainer.get(state);
                if (optional.isEmpty()) {
                    ArrayList<MethodTargetPair> methodTargetPairs = new ArrayList<>();
                    methodTargetPairs.add(new MethodTargetPair(method, bean));
                    methodContainer.put(state, methodTargetPairs);
                } else {
                    optional.get().add(new MethodTargetPair(method, bean));
                }
            }
        }
    }

    @Override
    protected List<Object> getBeans() {
        return handlers;
    }

    @Override
    protected void postProcessSavedObject(String state, List<MethodTargetPair> mappingMethods) {
        for (MethodTargetPair mappingMethod : mappingMethods) {
            Method method = mappingMethod.getMethod();
            String messageRegex = method.getAnnotation(UpdateMapping.class).messageRegex();
            log.info("Mapped state [\"{}\"] with message regex [\"{}\"] onto {}", state, messageRegex, method);
        }
    }

    private void validateDuplicates(String state, String messageRegex, Method method) {
        Optional<List<MethodTargetPair>> mappingMethodOptional = methodContainer.get(state);
        mappingMethodOptional.ifPresent(mappingMethod -> validateDuplicates(messageRegex, method, mappingMethod));
    }

    private void validateDuplicates(String messageRegex, Method method, List<MethodTargetPair> mappingMethod) {
        for (MethodTargetPair methodTargetPair : mappingMethod) {
            Method storedMethod = methodTargetPair.getMethod();
            UpdateMapping annotation = storedMethod.getAnnotation(UpdateMapping.class);
            String storedMethodMessageRegex = annotation.messageRegex();
            if (storedMethodMessageRegex.equals(messageRegex)) {
                String message = String.format("Found duplicate method annotated as @UpdateMapping with " +
                        "same state and messageRegex: %s and %s", storedMethod, method.getName());
                throw new IllegalStateException(message);
            }
        }
    }
}
