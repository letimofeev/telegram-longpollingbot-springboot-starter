package org.telegram.telegrambot.bot;

import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.handler.UpdateDispatcher;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;

public abstract class TelegramLongPollingBot extends org.telegram.telegrambots.bots.TelegramLongPollingBot {

    public abstract void executeAllApiMethods(List<PartialBotApiMethod<Message>> apiMethods);
}
