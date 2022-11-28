package org.telegram.telegrambot.handler;

import java.lang.reflect.Method;

public interface StateManager {

    void setNewStateIfRequired(long chatId, Method updateMappingMethod);
}
