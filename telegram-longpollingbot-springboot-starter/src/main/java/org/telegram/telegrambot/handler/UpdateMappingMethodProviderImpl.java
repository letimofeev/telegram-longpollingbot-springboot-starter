package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.container.UpdateMappingMethodContainer;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.repository.StateSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UpdateMappingMethodProviderImpl implements UpdateMappingMethodProvider {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodProviderImpl.class);

    private final StateSource stateSource;
    private final UpdateMappingMethodContainer mappingMethodContainer;

    public UpdateMappingMethodProviderImpl(StateSource stateSource, UpdateMappingMethodContainer mappingMethodContainer) {
        this.stateSource = stateSource;
        this.mappingMethodContainer = mappingMethodContainer;
    }

    @Override
    public Optional<InvocationUnit> getUpdateMappingMethod(Update update) {
        long chatId = update.getMessage().getChatId();
        String state = stateSource.getState(chatId);
        log.debug("Getting mapping method for state: \"{}\" and update: {}", state, update);
        String message = update.getMessage().getText();
        return getMessageMatchingMethod(update, state, message);
    }

    private Optional<InvocationUnit> getMessageMatchingMethod(Update update, String state, String message) {
        Optional<List<MethodTargetPair>> optional = mappingMethodContainer.get(state);
        if (optional.isPresent()) {
            for (MethodTargetPair methodTargetPair : optional.get()) {
                UpdateMapping annotation = methodTargetPair.getMethod().getAnnotation(UpdateMapping.class);
                String messageRegex = annotation.messageRegex();
                Pattern pattern = Pattern.compile(messageRegex);
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    List<Object> args = getTypedRegexGroups(methodTargetPair.getMethod(), matcher);
                    args.add(0, update);
                    return Optional.of(new InvocationUnit(methodTargetPair, args.toArray()));
                }
            }
        }
        return Optional.empty();
    }

    private List<Object> getTypedRegexGroups(Method method, Matcher matcher) {
        List<Object> args = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            RegexGroup annotation = parameter.getAnnotation(RegexGroup.class);
            if (annotation != null) {
                int groupNumber = annotation.value();
                Class<?> parameterType = parameter.getType();
                String group = matcher.group(groupNumber);
                Object arg = getTypedRegexGroup(group, parameterType);
                args.add(arg);
            }
        }
        return args;
    }

    private Object getTypedRegexGroup(String group, Class<?> parameterType) {
        if (parameterType == int.class) {
            return Integer.parseInt(group);
        } else if (parameterType == String.class) {
            return group;
        } else {
            throw new IllegalArgumentException("Unsupported annotated parameter type: " +
                    parameterType.getSimpleName());
        }
    }
}