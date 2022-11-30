package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambot.dto.InvocationUnit;

import java.util.Optional;

public interface UpdateMappingMethodProvider<T> {

    Optional<InvocationUnit> getUpdateMappingMethod(T apiObject);

    UpdateType getUpdateType();
}
