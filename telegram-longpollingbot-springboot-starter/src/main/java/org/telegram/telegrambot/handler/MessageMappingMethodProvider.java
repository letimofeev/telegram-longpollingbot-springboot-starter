package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public interface MessageMappingMethodProvider {

    Optional<InvocationUnit> getMessageMappingMethod(Message message);
}
