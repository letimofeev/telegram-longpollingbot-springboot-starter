package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface UpdateMappingMethodArgumentResolver {

    boolean isParameterSupported(Parameter parameter);

    Object resolveArgument(Parameter parameter, Method method, Update update);
}
