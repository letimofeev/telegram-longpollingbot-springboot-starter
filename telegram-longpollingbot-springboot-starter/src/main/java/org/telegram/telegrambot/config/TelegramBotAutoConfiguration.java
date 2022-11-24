package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.bot.DispatchedTelegramLongPollingBot;
import org.telegram.telegrambot.bot.TelegramLongPollingBotExtended;
import org.telegram.telegrambot.handler.BotApiMethodExecutorResolver;
import org.telegram.telegrambot.handler.UpdateResolver;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ComponentScan("org.telegram.telegrambot")
@AutoConfigureAfter(TelegramBotStarterConfiguration.class)
public class TelegramBotAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("telegrambot.token")
    public TelegramLongPollingBotExtended longPollingBot(@Value("${telegrambot.username}}") String botUsername,
                                                         @Value("${telegrambot.token}") String botToken,
                                                         UpdateResolver updateResolver,
                                                         BotApiMethodExecutorResolver methodExecutorResolver) {
        return new DispatchedTelegramLongPollingBot(botUsername, botToken, updateResolver, methodExecutorResolver);
    }
}
