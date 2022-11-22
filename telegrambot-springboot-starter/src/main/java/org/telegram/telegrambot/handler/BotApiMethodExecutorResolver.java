package org.telegram.telegrambot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;

public interface BotApiMethodExecutorResolver {

    Method getApiMethodExecutionMethod(PartialBotApiMethod<Message> apiMethod);
}
