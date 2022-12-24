package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateMappingHandlerResolver implements UpdateHandlerResolver {

    private final List<UpdateMappingMethodInterceptor> allInterceptors;
    private final UpdateMappingMethodResolver methodResolver;

    public UpdateMappingHandlerResolver(List<UpdateMappingMethodInterceptor> allInterceptors,
                                        UpdateMappingMethodResolver methodResolver) {
        this.allInterceptors = allInterceptors;
        this.methodResolver = methodResolver;
    }

    @Override
    public UpdateHandlerExecutionChain getHandler(Update update) {
        List<UpdateMappingMethodInterceptor> interceptors = getInterceptors(update);
        InvocableUpdateMappingMethod mappingMethod = methodResolver.resolveMappingMethod(update);
        return new UpdateHandlerExecutionChain(interceptors, mappingMethod);
    }

    private List<UpdateMappingMethodInterceptor> getInterceptors(Update update) {
        List<UpdateMappingMethodInterceptor> interceptors = new ArrayList<>();
        for (UpdateMappingMethodInterceptor interceptor : allInterceptors) {
            if (interceptor.isUpdateSupported(update)) {
                interceptors.add(interceptor);
            }
        }
        return interceptors;
    }

}
