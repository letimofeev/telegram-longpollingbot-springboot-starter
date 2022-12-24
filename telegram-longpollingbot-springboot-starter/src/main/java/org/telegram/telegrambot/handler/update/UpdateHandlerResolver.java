package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandlerResolver {

    UpdateHandlerExecutionChain getHandler(Update update);
}
