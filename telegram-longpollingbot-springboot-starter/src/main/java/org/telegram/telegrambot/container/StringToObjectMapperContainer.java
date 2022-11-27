package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.databind.StringToObjectMapper;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class StringToObjectMapperContainer extends AbstractContainer<Class<?>, StringToObjectMapper<?>> {

    public StringToObjectMapperContainer(List<StringToObjectMapper<?>> objectMappers) {
        Map<Class<?>, StringToObjectMapper<?>> all = objectMappers.stream().collect(toMap(StringToObjectMapper::getType, identity()));
        this.container.putAll(all);
    }
}
