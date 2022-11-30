package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.handler.update.UpdateMappingMethodProvider;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class UpdateMappingMethodProviderContainer extends AbstractContainer<Class<? extends BotApiObject>, UpdateMappingMethodProvider<? extends BotApiObject>> {

    public UpdateMappingMethodProviderContainer(List<UpdateMappingMethodProvider<? extends BotApiObject>> mappingMethodProviders) {
        Map<Class<? extends BotApiObject>, UpdateMappingMethodProvider<? extends BotApiObject>> all = mappingMethodProviders.stream()
                .collect(toMap(UpdateMappingMethodProvider::getUpdateType, identity()));
        this.container.putAll(all);
    }
}
