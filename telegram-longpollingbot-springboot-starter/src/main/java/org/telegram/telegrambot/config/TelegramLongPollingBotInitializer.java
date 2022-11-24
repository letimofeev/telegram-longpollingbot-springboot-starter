package org.telegram.telegrambot.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.List;

@Component
public class TelegramLongPollingBotInitializer implements InitializingBean {

    private final List<TelegramLongPollingBot> bots;
    private final TelegramBotsApi telegramBotsApi;

    public TelegramLongPollingBotInitializer(List<TelegramLongPollingBot> bots, TelegramBotsApi telegramBotsApi) {
        this.bots = bots;
        this.telegramBotsApi = telegramBotsApi;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (TelegramLongPollingBot bot : bots) {
            telegramBotsApi.registerBot(bot);
        }
    }
}
