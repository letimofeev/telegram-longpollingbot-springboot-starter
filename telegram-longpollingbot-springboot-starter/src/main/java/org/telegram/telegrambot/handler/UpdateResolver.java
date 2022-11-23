package org.telegram.telegrambot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface UpdateResolver {

    List<? extends PartialBotApiMethod<Message>> getResponse(Update update);
}
