package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

@Component
public class UpdateMappingMethodResolver {

    private final List<UpdateMappingMethodInfoFilterProvider> filterProviders;
    private final UpdateMappingInfoRegistry mappingInfoRegistry;
    private final UpdateMappingMethodArgumentResolverComposite argumentResolver;

    public UpdateMappingMethodResolver(List<UpdateMappingMethodInfoFilterProvider> filterProviders,
                                       UpdateMappingInfoRegistry mappingInfoRegistry,
                                       UpdateMappingMethodArgumentResolverComposite argumentResolver) {
        this.filterProviders = filterProviders;
        this.mappingInfoRegistry = mappingInfoRegistry;
        this.argumentResolver = argumentResolver;
    }

    public InvocableUpdateMappingMethod resolveMappingMethod(Update update) {
        UpdateMappingMethodInfoFilterChain mappingFilterChain = new UpdateMappingMethodInfoFilterChain();
        for (UpdateMappingMethodInfoFilterProvider filterProvider : filterProviders) {
            Predicate<UpdateMappingMethodInfo> filter = filterProvider.getFilter(update);
            mappingFilterChain.addFilter(filter);
        }
        List<UpdateMappingMethodInfo> mappingInfos = mappingInfoRegistry.getFilteredMappingInfos(mappingFilterChain);
        if (mappingInfos.isEmpty()) {
            throw new NoUpdateHandlerFoundException();
        }
        UpdateMappingMethodInfo methodInfo = mappingInfos.get(0);
        Method method = methodInfo.getMethod();
        Object[] args = argumentResolver.resolveArguments(method, update);
        return new InvocableUpdateMappingMethod(methodInfo, args);
    }
}
