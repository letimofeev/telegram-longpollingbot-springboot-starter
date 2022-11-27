package org.telegram.telegrambot.handler;

import org.telegram.telegrambot.model.InvocationUnit;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface ApiMethodsReturningMethodInvoker {

    List<? extends PartialBotApiMethod<Message>> invokeMethod(InvocationUnit invocationUnit);
}
