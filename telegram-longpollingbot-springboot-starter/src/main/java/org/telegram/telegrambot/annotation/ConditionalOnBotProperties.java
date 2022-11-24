package org.telegram.telegrambot.annotation;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnBotPropertiesConditional.class)
public @interface ConditionalOnBotProperties {
}
