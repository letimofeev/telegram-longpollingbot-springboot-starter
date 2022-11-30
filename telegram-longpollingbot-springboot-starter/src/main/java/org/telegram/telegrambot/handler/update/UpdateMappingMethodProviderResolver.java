package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.Optional;

public interface UpdateMappingMethodProviderResolver {

    <T extends BotApiObject> Optional<InvocationUnit> getUpdateMappingMethod(T apiObject, UpdateType updateType);
}
