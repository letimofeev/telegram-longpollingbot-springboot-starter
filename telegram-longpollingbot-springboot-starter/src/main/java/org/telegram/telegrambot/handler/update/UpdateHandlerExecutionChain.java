package org.telegram.telegrambot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class UpdateHandlerExecutionChain {

    private final List<UpdateMappingMethodInterceptor> interceptors;
    private final InvocableUpdateMappingMethod mappingMethod;


    public UpdateHandlerExecutionChain(List<UpdateMappingMethodInterceptor> interceptors,
                                       InvocableUpdateMappingMethod mappingMethod) {
        this.interceptors = interceptors;
        this.mappingMethod = mappingMethod;
    }

    public List<UpdateMappingMethodInterceptor> getInterceptors() {
        return interceptors;
    }

    public InvocableUpdateMappingMethod getMappingMethod() {
        return mappingMethod;
    }

    public void applyPreHandle(Update update) {
        for (UpdateMappingMethodInterceptor interceptor : interceptors) {
            interceptor.applyPreHandle(update);
        }
    }

    public void applyPostHandle(Update update) {
        for (UpdateMappingMethodInterceptor interceptor : interceptors) {
            interceptor.applyPostHandle(update);
        }
    }
}
