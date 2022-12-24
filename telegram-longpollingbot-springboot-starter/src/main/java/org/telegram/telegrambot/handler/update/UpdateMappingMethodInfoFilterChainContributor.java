package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateMappingMethodInfoFilterChainContributor {

    void addFilter(Update update, UpdateMappingMethodInfoFilterChain filterChain);
}
