package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.handler.update.UpdateMappingInvocationUnitProvider;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class UpdateMappingInvocationUnitProviderContainer extends AbstractContainer<Class<? extends BotApiObject>, UpdateMappingInvocationUnitProvider<? extends BotApiObject>> {

    public UpdateMappingInvocationUnitProviderContainer(List<UpdateMappingInvocationUnitProvider<? extends BotApiObject>> providers) {
        Map<Class<? extends BotApiObject>, UpdateMappingInvocationUnitProvider<? extends BotApiObject>> all = providers.stream()
                .collect(toMap(UpdateMappingInvocationUnitProvider::getUpdateType, identity()));
        this.container.putAll(all);
    }
}
