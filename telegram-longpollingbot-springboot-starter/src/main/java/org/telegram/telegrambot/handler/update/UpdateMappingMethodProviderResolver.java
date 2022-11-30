package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public interface UpdateMappingMethodProviderResolver {

    <T extends BotApiObject> InvocationUnit getUpdateMappingMethod(T apiObject, UpdateType updateType);
}
