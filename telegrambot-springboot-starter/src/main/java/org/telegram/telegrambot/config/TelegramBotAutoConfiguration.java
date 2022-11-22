package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.UpdateHandler;
import org.telegram.telegrambot.bot.TelegramBot;
import org.telegram.telegrambot.handler.*;
import org.telegram.telegrambot.state.StateSource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Configuration
@ConditionalOnProperty(
        prefix = "telegrambot",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(TelegramBotProperties.class)
public class TelegramBotAutoConfiguration {

    @Value("${telegrambot.username}}")
    private String botUsername;

    @Value("${telegrambot.token}")
    private String botToken;

    @Autowired
    @UpdateHandler
    private List<Object> handlers;

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public BotApiMethodExecutorResolver executeMethodResolver() {
        return new TelegramBotExecuteBotApiMethodResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateMappingMethodSelector methodSelector(StateSource stateSource) {
        return new TelegramBotUpdateMappingMethodSelector(stateSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateMappingMethodApplier methodApplier() {
        return new TelegramBotUpdateMappingMethodApplier();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateDispatcher updateDispatcher(UpdateMappingMethodSelector methodSelector,
                                             UpdateMappingMethodApplier methodApplier,
                                             BotApiMethodExecutorResolver methodExecutorResolver) {
        return new TelegramBotUpdateDispatcher(handlers, methodSelector, methodApplier, methodExecutorResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("telegrambot.token")
    public TelegramBot telegramBot(TelegramBotsApi telegramBotsApi, UpdateDispatcher updateDispatcher) {
        return new TelegramBot(botUsername, botToken, telegramBotsApi, updateDispatcher);
    }
}
