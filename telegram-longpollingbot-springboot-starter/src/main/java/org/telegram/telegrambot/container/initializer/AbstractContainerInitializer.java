package org.telegram.telegrambot.container.initializer;

import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambot.container.AbstractContainer;
import org.telegram.telegrambot.validator.MethodSignatureValidator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractContainerInitializer<K, V> implements InitializingBean {

    protected final AbstractContainer<K, V> methodContainer;

    protected AbstractContainerInitializer(AbstractContainer<K, V> methodContainer) {
        this.methodContainer = methodContainer;
    }

    @Override
    public void afterPropertiesSet() {
        for (Object bean : getBeans()) {
            processBean(bean);
        }
        postProcessSavedObjects();
    }

    protected abstract void processBean(Object bean);

    protected abstract List<Object> getBeans();

    protected void postProcessSavedObjects() {
        Set<Map.Entry<K, V>> entrySet = methodContainer.getEntrySet();
        for (Map.Entry<K, V> entry : entrySet) {
            K key = entry.getKey();
            V value = entry.getValue();
            postProcessSavedObject(key, value);
        }
    }

    protected abstract void postProcessSavedObject(K key, V value);
}
