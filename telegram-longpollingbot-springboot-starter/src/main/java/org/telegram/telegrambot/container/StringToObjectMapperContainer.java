package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambot.converter.StringToObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class StringToObjectMapperContainer {

    private final Map<Class<?>, StringToObjectMapper<?>> container = new ConcurrentHashMap<>();

    public StringToObjectMapperContainer(List<StringToObjectMapper<?>> objectMappers) {
        Map<Class<?>, StringToObjectMapper<?>> all = objectMappers.stream().collect(toMap(StringToObjectMapper::getType, identity()));
        this.container.putAll(all);
    }

    public Optional<StringToObjectMapper<?>> get(Class<?> key) {
        Class<?> resolvedPrimitiveClass = ClassUtils.resolvePrimitiveIfNecessary(key);
        StringToObjectMapper<?> value = container.get(resolvedPrimitiveClass);
        return Optional.ofNullable(value);
    }

    public void put(Class<?> key, StringToObjectMapper<?> value) {
        container.put(key, value);
    }

}
