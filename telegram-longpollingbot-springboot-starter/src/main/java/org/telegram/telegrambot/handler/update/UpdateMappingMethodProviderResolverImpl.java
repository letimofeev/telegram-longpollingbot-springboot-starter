package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.UpdateMappingMethodProviderContainer;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

@Component
public class UpdateMappingMethodProviderResolverImpl implements UpdateMappingMethodProviderResolver {

    private final UpdateMappingMethodProviderContainer methodProviderContainer;

    public UpdateMappingMethodProviderResolverImpl(UpdateMappingMethodProviderContainer methodProviderContainer) {
        this.methodProviderContainer = methodProviderContainer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BotApiObject> InvocationUnit getUpdateMappingMethod(T apiObject, UpdateType updateType) {
        return methodProviderContainer.get(updateType)
                .map(methodProvider -> (UpdateMappingMethodProvider<T>) methodProvider)
                .flatMap(methodProvider -> methodProvider.getUpdateMappingMethod(apiObject))
                .orElseThrow(() -> new NoUpdateHandlerFoundException(String.format("No handlers found for update type %s " +
                        "and update: %s", updateType, apiObject)));
    }
}
