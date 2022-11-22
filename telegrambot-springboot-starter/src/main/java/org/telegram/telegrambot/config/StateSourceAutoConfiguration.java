package org.telegram.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.state.DefaultStateSource;
import org.telegram.telegrambot.state.StateSource;

@Configuration
public class StateSourceAutoConfiguration {

    @Value("${telegrambot.initial-state}")
    private String initialState;

    @Bean
    @ConditionalOnMissingBean
    public StateSource stateSource() {
        return new DefaultStateSource(initialState);
    }
}
