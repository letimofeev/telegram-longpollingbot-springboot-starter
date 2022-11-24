package org.telegram.telegrambot.annotation;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class OnBotPropertiesConditional extends AllNestedConditions {

    public OnBotPropertiesConditional() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty("telegrambot.token")
    public static class Token {
    }

    @ConditionalOnProperty("telegrambot.username")
    public static class Username {
    }
}
