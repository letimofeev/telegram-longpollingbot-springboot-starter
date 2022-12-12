package org.telegram.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LongPollingBotApiObjectExtractor implements BotApiObjectExtractor {

    @Override
    public BotApiObject extract(Update update) {
        if (update.hasMessage()) {
            return update.getMessage();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
