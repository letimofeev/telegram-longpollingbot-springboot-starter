package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.UpdateHandlerAnnotationBeanPostProcessor;
import org.telegram.telegrambot.bot.DispatchedTelegramLongPollingBot;
import org.telegram.telegrambot.bot.TelegramLongPollingBotExtended;
import org.telegram.telegrambot.handler.*;
import org.telegram.telegrambot.model.UpdateMappingMethodContainer;
import org.telegram.telegrambot.repository.StateSource;
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
    public ApiMethodsReturningMethodInvoker methodInvoker() {
        return new ApiMethodsReturningMethodInvokerImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateResolver updateDispatcher(StateSource stateSource,
                                           UpdateMappingMethodContainer mappingMethodContainer,
                                           ApiMethodsReturningMethodInvoker methodInvoker) {
        return new LongPollingBotUpdateResolver(stateSource, mappingMethodContainer, methodInvoker);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramLongPollingBotInitializer telegramBotInitializer(List<org.telegram.telegrambots.bots.TelegramLongPollingBot> bots, TelegramBotsApi telegramBotsApi) {
        return new TelegramLongPollingBotInitializer(bots, telegramBotsApi);
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
