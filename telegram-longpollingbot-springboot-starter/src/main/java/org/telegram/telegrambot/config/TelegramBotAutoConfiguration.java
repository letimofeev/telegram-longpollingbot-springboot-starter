package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.UpdateHandlerAnnotationBeanPostProcessor;
import org.telegram.telegrambot.bot.DispatchedTelegramLongPollingBot;
import org.telegram.telegrambot.handler.*;
import org.telegram.telegrambot.model.UpdateMappingMethodContainer;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Configuration
public class TelegramBotAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public UpdateMappingMethodContainer updateMappingMethodContainer() {
        return new UpdateMappingMethodContainer();
    }

    @Bean
    public UpdateHandlerAnnotationBeanPostProcessor updateHandlerAnnotationBeanPostProcessor(UpdateMappingMethodContainer methodInvokerContainer) {
        return new UpdateHandlerAnnotationBeanPostProcessor(methodInvokerContainer);
    }

    @Bean
    @ConditionalOnMissingBean
    public BotApiMethodExecutorResolver executeMethodResolver() {
        return new LongPollingBotApiMethodExecutorResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateMappingMethodInvoker methodInvoker() {
        return new LongPollingBotUpdateMappingMethodInvoker();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateDispatcher updateDispatcher(StateSource stateSource,
                                             UpdateMappingMethodContainer mappingMethodContainer,
                                             UpdateMappingMethodInvoker methodInvoker,
                                             BotApiMethodExecutorResolver methodExecutorResolver) {
        return new LongPollingBotUpdateDispatcher(stateSource, mappingMethodContainer, methodInvoker, methodExecutorResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramLongPollingBotInitializer telegramBotInitializer(List<TelegramLongPollingBot> bots, TelegramBotsApi telegramBotsApi) {
        return new TelegramLongPollingBotInitializer(bots, telegramBotsApi);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("telegrambot.token")
    public DispatchedTelegramLongPollingBot longPollingBot(@Value("${telegrambot.username}}") String botUsername,
                                                           @Value("${telegrambot.token}") String botToken,
                                                           UpdateDispatcher updateDispatcher) {
        return new DispatchedTelegramLongPollingBot(botUsername, botToken, updateDispatcher);
    }
}
