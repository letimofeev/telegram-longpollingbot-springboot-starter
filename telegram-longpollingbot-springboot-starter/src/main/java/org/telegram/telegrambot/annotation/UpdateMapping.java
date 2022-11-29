package org.telegram.telegrambot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.telegram.telegrambot.repository.BotState.ANY_STATE;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdateMapping {

    String state() default ANY_STATE;

    String messageRegex() default "";

    String newState() default "";
}
