package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface UpdateMappingMethodProvider {

    Optional<InvocationUnit> getUpdateMappingMethod(Update update);
}
