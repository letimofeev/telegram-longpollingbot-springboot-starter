package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambot.dto.InvocationUnit;

import java.util.Optional;

public interface UpdateMappingInvocationUnitProvider<T> {

    Optional<InvocationUnit> getUpdateMappingInvocationUnit(T apiObject);

    Class<T> getUpdateType();
}
