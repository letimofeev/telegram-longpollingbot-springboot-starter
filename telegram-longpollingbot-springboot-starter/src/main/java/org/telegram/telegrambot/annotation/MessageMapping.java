package org.telegram.telegrambot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.telegram.telegrambot.repository.BotState.DEFAULT_INITIAL_STATE;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMapping {

    String state() default DEFAULT_INITIAL_STATE;

    String messageRegex() default "";

    String newState() default "";
}
