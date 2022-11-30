package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.container.UpdateMappingMethodProviderContainer;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.Optional;

@Component
public class UpdateMappingMethodProviderResolverImpl implements UpdateMappingMethodProviderResolver {

    private final UpdateMappingMethodProviderContainer methodProviderContainer;

    public UpdateMappingMethodProviderResolverImpl(UpdateMappingMethodProviderContainer methodProviderContainer) {
        this.methodProviderContainer = methodProviderContainer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BotApiObject> Optional<InvocationUnit> getUpdateMappingMethod(T apiObject, UpdateType updateType) {
        return methodProviderContainer.get(updateType)
                .map(methodProvider -> (UpdateMappingMethodProvider<T>) methodProvider)
                .flatMap(methodProvider -> methodProvider.getUpdateMappingMethod(apiObject));
    }
}
