package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface UpdateMappingMethodInvoker {

    List<PartialBotApiMethod<Message>> invokeUpdateMappingMethod(Update update, MethodTargetPair mappingMethod);
}
