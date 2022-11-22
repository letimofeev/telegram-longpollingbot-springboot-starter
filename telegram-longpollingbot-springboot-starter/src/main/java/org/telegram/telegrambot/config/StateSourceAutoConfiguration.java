package org.telegram.telegrambot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambot.repository.DefaultStateSource;
import org.telegram.telegrambot.repository.StateSource;

@Configuration
public class StateSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StateSource stateSource() {
        return new DefaultStateSource("");
    }
}
