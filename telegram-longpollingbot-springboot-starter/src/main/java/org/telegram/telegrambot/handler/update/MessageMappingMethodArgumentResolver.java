package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
public class MessageMappingMethodArgumentResolver implements UpdateMappingMethodArgumentResolver {
    @Override
    public boolean isParameterSupported(Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        return Message.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolveArgument(Parameter parameter, Method method, Update update) {
        return update.getMessage();
    }
}
