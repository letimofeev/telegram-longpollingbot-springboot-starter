package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface ApiMethodsReturningMethodInvoker {

    List<? extends PartialBotApiMethod<Message>> invokeMethod(MethodTargetPair mappingMethod, Object... args);
}
