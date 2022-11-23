package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.repository.DefaultStateSource;
import org.telegram.telegrambot.repository.StateSource;

@Configuration
public class StateSourceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StateSource stateSource(@Value("telegrambot.initial-state:") String initialState) {
        return new DefaultStateSource(initialState);
    }
}
