package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UpdateMappingMethodArgumentResolverComposite {

    private final List<UpdateMappingMethodArgumentResolver> resolvers;

    public UpdateMappingMethodArgumentResolverComposite(List<UpdateMappingMethodArgumentResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public Object[] resolveArguments(Method method, Update update) {
        List<Object> args = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            lookupResolver(parameter)
                    .map(resolver -> resolver.resolveArgument(parameter, method, update))
                    .ifPresent(args::add);
        }
        return args.toArray();
    }

    private Optional<UpdateMappingMethodArgumentResolver> lookupResolver(Parameter parameter) {
        for (UpdateMappingMethodArgumentResolver resolver : resolvers) {
            if (resolver.isParameterSupported(parameter)) {
                return Optional.of(resolver);
            }
        }
        return Optional.empty();
    }
}
