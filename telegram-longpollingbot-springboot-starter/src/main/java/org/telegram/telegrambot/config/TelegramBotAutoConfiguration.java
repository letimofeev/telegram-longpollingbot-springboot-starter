package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.annotation.UpdateHandlerAnnotationBeanPostProcessor;
import org.telegram.telegrambot.bot.UpdateDispatcherTelegramLongPollingBot;
import org.telegram.telegrambot.handler.*;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambot.repository.UpdateMappingMethodContainer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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
public class TelegramBotAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public UpdateMappingMethodContainer methodInvokerContainer() {
        return new UpdateMappingMethodContainer();
    }

    @Bean
    public UpdateMappingMethodSignatureValidator methodSignatureValidator() {
        return new LongPollingBotUpdateMappingMethodSignatureValidator();
    }

    @Bean
    public UpdateHandlerAnnotationBeanPostProcessor updateHandlerAnnotationBeanPostProcessor(UpdateMappingMethodContainer methodInvokerContainer,
                                                                                             UpdateMappingMethodSignatureValidator methodSignatureValidator) {
        return new UpdateHandlerAnnotationBeanPostProcessor(methodInvokerContainer, methodSignatureValidator);
    }

    @Bean
    @ConditionalOnMissingBean
    public BotApiMethodExecutorResolver executeMethodResolver() {
        return new LongPollingBotExecuteBotApiMethodResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateMappingMethodSelector methodSelector() {
        return new LongPollingBotUpdateMappingMethodSelector();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateMappingMethodInvoker methodApplier() {
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
    public UpdateDispatcherTelegramLongPollingBot longPollingBot(@Value("${telegrambot.username}}") String botUsername,
                                                                 @Value("${telegrambot.token}") String botToken,
                                                                 UpdateDispatcher updateDispatcher) {
        return new UpdateDispatcherTelegramLongPollingBot(botUsername, botToken, updateDispatcher);
    }
}
