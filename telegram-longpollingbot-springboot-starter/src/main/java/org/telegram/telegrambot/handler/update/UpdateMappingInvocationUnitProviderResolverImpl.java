package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.UpdateMappingInvocationUnitProviderContainer;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

@Component
public class UpdateMappingInvocationUnitProviderResolverImpl implements UpdateMappingInvocationUnitProviderResolver {

    private final UpdateMappingInvocationUnitProviderContainer methodProviderContainer;

    public UpdateMappingInvocationUnitProviderResolverImpl(UpdateMappingInvocationUnitProviderContainer methodProviderContainer) {
        this.methodProviderContainer = methodProviderContainer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BotApiObject> InvocationUnit getUpdateMappingMethod(T apiObject) {
        return methodProviderContainer.get(apiObject.getClass())
                .map(methodProvider -> (UpdateMappingInvocationUnitProvider<T>) methodProvider)
                .flatMap(methodProvider -> methodProvider.getUpdateMappingInvocationUnit(apiObject))
                .orElseThrow(() -> new NoUpdateHandlerFoundException(String.format("No handlers found for " +
                        "update: %s", apiObject)));
    }
}
