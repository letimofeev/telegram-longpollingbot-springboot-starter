package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

public interface UpdateMappingMethodInfoFilterProvider {

    Predicate<UpdateMappingMethodInfo> getFilter(Update update);
}
