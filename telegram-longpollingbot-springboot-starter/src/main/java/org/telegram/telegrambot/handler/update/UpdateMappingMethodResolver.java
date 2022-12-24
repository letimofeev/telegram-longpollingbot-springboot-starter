package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.expection.NoUpdateHandlerFoundException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.List;

@Component
public class UpdateMappingMethodResolver {

    private final List<UpdateMappingMethodInfoFilterChainContributor> filterChainContributors;
    private final UpdateMappingInfoRegistry mappingInfoRegistry;
    private final UpdateMappingMethodArgumentResolverComposite argumentResolver;

    public UpdateMappingMethodResolver(List<UpdateMappingMethodInfoFilterChainContributor> filterChainContributors,
                                       UpdateMappingInfoRegistry mappingInfoRegistry,
                                       UpdateMappingMethodArgumentResolverComposite argumentResolver) {
        this.filterChainContributors = filterChainContributors;
        this.mappingInfoRegistry = mappingInfoRegistry;
        this.argumentResolver = argumentResolver;
    }

    public InvocableUpdateMappingMethod resolveMappingMethod(Update update) {
        UpdateMappingMethodInfoFilterChain mappingFilterChain = new UpdateMappingMethodInfoFilterChain();
        for (UpdateMappingMethodInfoFilterChainContributor filterChainContributor : filterChainContributors) {
            filterChainContributor.addFilter(update, mappingFilterChain);
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
