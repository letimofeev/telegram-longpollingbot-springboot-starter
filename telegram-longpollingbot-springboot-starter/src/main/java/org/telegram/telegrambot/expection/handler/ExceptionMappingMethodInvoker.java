package org.telegram.telegrambot.expection.handler;

import org.telegram.telegrambot.model.MethodTargetPair;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ExceptionMappingMethodInvoker {

    void invokeExceptionMappingMethod(MethodTargetPair exceptionMapping, Update update, Exception e);
}
