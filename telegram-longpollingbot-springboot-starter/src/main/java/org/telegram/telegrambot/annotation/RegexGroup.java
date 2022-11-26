package org.telegram.telegrambot.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

@Target(value = {PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegexGroup {

    int value();
}
