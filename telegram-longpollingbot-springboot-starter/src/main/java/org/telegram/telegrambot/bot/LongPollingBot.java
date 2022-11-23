package org.telegram.telegrambot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public abstract class LongPollingBot extends TelegramLongPollingBot {

    public abstract void executeApiMethod(PartialBotApiMethod<Message> apiMethod);

    public abstract void executeAllApiMethods(List<? extends PartialBotApiMethod<Message>> apiMethods);
}
