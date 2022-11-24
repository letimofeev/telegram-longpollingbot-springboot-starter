package org.telegram.telegrambot.container.initializer;

import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambot.container.MethodTargetPairContainer;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.validator.MethodSignatureValidator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MethodTargetPairContainerInitializer<T> implements InitializingBean {

    protected final MethodTargetPairContainer<T> methodContainer;
    protected final MethodSignatureValidator methodSignatureValidator;

    protected MethodTargetPairContainerInitializer(MethodTargetPairContainer<T> methodContainer,
                                                   MethodSignatureValidator methodSignatureValidator) {
        this.methodContainer = methodContainer;
        this.methodSignatureValidator = methodSignatureValidator;
    }

    @Override
    public void afterPropertiesSet() {
        for (Object bean : getBeans()) {
            processBean(bean);
        }
        postprocessSavedMethodTargetPairs();
    }

    protected abstract void processBean(Object bean);

    protected abstract List<Object> getBeans();

    protected void postprocessSavedMethodTargetPairs() {
        Set<Map.Entry<T, MethodTargetPair>> entrySet = methodContainer.getEntrySet();
        for (Map.Entry<T, MethodTargetPair> methodTargetPairEntry : entrySet) {
            T key = methodTargetPairEntry.getKey();
            MethodTargetPair methodTargetPair = methodTargetPairEntry.getValue();
            processSavedMethodTargetPair(key, methodTargetPair);
        }
    }

    protected abstract void processSavedMethodTargetPair(T key, MethodTargetPair methodTargetPair);
}
