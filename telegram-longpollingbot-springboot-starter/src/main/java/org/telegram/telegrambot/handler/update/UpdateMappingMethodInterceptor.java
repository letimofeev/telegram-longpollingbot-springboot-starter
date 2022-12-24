package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateMappingMethodInterceptor {

    default boolean isUpdateSupported(Update update) {
        return true;
    }

    default void applyPreHandle(Update update) {
    }

    default void applyPostHandle(Update update) {
    }
}
