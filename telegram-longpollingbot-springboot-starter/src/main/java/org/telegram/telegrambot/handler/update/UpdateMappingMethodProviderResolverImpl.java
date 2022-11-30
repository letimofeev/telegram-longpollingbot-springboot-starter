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
    public <T extends BotApiObject> Optional<InvocationUnit> getUpdateMappingMethod(T apiObject, UpdateType updateType) {
        Optional<UpdateMappingMethodProvider<? extends BotApiObject>> methodProviderOptional = methodProviderContainer.get(updateType);
        if (methodProviderOptional.isEmpty()) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        UpdateMappingMethodProvider<T> methodProvider = (UpdateMappingMethodProvider<T>) methodProviderOptional.get();
        return methodProvider.getUpdateMappingMethod(apiObject);
    }
}