package org.telegram.telegrambot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

public interface UpdateMappingMethodApplier {

    PartialBotApiMethod<Message> applyHandlerMappingMethod(Update update, Method method, Object handler);
}
