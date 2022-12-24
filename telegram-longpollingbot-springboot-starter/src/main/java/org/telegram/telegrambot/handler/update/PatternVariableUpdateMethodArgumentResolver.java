package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.telegram.telegrambot.annotation.MessageMapping;
import org.telegram.telegrambot.container.StringToObjectMapperContainer;
import org.telegram.telegrambot.converter.StringToObjectMapper;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class PatternVariableUpdateMethodArgumentResolver implements UpdateMappingMethodArgumentResolver {

    private final StringToObjectMapperContainer objectMapperContainer;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public PatternVariableUpdateMethodArgumentResolver(StringToObjectMapperContainer objectMapperContainer) {
        this.objectMapperContainer = objectMapperContainer;
    }

    @Override
    public boolean isParameterSupported(Parameter parameter) {
        return parameter.isAnnotationPresent(PatternVariable.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter, Method method, Update update) {
        String stringArg = resolveParameterByName(parameter, method, update);
        Class<?> parameterType = parameter.getType();
        Optional<StringToObjectMapper<?>> stringToObjectMapper = objectMapperContainer.get(parameterType);
        if (stringToObjectMapper.isPresent()) {
            return stringToObjectMapper.get().mapToObject(stringArg);
        }
        throw new UnsupportedOperationException(String.format("Unsupported annotated as @RegexGroup parameter type: %s. You should " +
                "implement StringToObjectMapper with this parameter type and add it to spring context via " +
                "@Component, @Bean or any other way", parameter.getName()));
    }

    protected String resolveParameterByName(Parameter parameter, Method method, Update update) {
        String parameterName = parameter.getName();
        String text = getMessageText(update);
        String pattern = getTextMessagePattern(method);
        Map<String, String> matchedVariables = matcher.extractUriTemplateVariables(pattern, text);
        return matchedVariables.get(parameterName);
    }
    
    private String getTextMessagePattern(Method method) {
        MessageMapping annotation = method.getAnnotation(MessageMapping.class);
        if (annotation == null) {
            throw new IllegalStateException("No MessageMapping annotation");
        }
        return annotation.messagePattern();
    }

    private String getMessageText(Update update) {
        Message message = update.getMessage();
        Objects.requireNonNull(message, "Message must not be null");
        String text = message.getText();
        Objects.requireNonNull(text, "Message text must not be null");
        return text;
    }
}
