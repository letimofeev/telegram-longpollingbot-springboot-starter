package org.telegram.telegrambot.handler.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class UpdateMappingMethodInvoker {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodInvoker.class);

    @SuppressWarnings("unchecked")
    public List<? extends PartialBotApiMethod<Message>> invokeMethod(InvocableUpdateMappingMethod mappingMethod) {
        Object apiMethods = mappingMethod.invokeMethod();
        Objects.requireNonNull(apiMethods, String.format("Method supposed to return api methods %s returned null", mappingMethod));

        if (apiMethods instanceof Collection) {
            log.trace("Method {} returned collection of api methods", mappingMethod);
            return List.copyOf((Collection<? extends PartialBotApiMethod<Message>>) apiMethods);
        }
        log.trace("Method {} returned single api method", mappingMethod);
        return List.of((PartialBotApiMethod<Message>) apiMethods);
    }
}
