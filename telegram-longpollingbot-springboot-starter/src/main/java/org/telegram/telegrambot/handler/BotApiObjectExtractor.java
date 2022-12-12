package org.telegram.telegrambot.handler;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotApiObjectExtractor {

    BotApiObject extract(Update update);
}
