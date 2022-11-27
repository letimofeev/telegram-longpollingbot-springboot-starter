package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.container.StringToObjectMapperContainer;
import org.telegram.telegrambot.container.UpdateMappingMethodContainer;
import org.telegram.telegrambot.databind.StringToObjectMapper;
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
    private final StringToObjectMapperContainer mapperContainer;

    public UpdateMappingMethodProviderImpl(StateSource stateSource, UpdateMappingMethodContainer mappingMethodContainer, StringToObjectMapperContainer mapperContainer) {
        this.stateSource = stateSource;
        this.mappingMethodContainer = mappingMethodContainer;
        this.mapperContainer = mapperContainer;
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
            List<MethodTargetPair> storedMappingMethods = optional.get();
            for (MethodTargetPair methodTargetPair : storedMappingMethods) {
                UpdateMapping annotation = methodTargetPair.getMethod().getAnnotation(UpdateMapping.class);
                String messageRegex = annotation.messageRegex();
                if (!messageRegex.isEmpty()) {
                    Optional<InvocationUnit> mappingWithRegexMatching = getMappingWithRegexMatching(update, message, messageRegex, methodTargetPair);
                    if (mappingWithRegexMatching.isPresent()) {
                        return mappingWithRegexMatching;
                    }
                }
            }
            return getMappingWithoutRegexMatching(update, storedMappingMethods);
        }
        return Optional.empty();
    }

    private Optional<InvocationUnit> getMappingWithRegexMatching(Update update, String message, String messageRegex,
                                                                 MethodTargetPair methodTargetPair) {
        Pattern pattern = Pattern.compile(messageRegex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            List<Object> args = getTypedRegexGroups(methodTargetPair.getMethod(), matcher);
            args.add(0, update);
            return Optional.of(new InvocationUnit(methodTargetPair, args.toArray()));
        }
        return Optional.empty();
    }

    private Optional<InvocationUnit> getMappingWithoutRegexMatching(Update update, List<MethodTargetPair> storedMappingMethods) {
        for (MethodTargetPair methodTargetPair : storedMappingMethods) {
            UpdateMapping annotation = methodTargetPair.getMethod().getAnnotation(UpdateMapping.class);
            String messageRegex = annotation.messageRegex();
            if (messageRegex.isEmpty()) {
                Object[] args = {update};
                return Optional.of(new InvocationUnit(methodTargetPair, args));
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
                validateGroupNumber(groupNumber, matcher);
                Class<?> parameterType = parameter.getType();
                String group = matcher.group(groupNumber);
                Object arg = getTypedRegexGroup(group, parameterType);
                args.add(arg);
            }
        }
        return args;
    }

    private Object getTypedRegexGroup(String group, Class<?> parameterType) {
        parameterType = ClassUtils.resolvePrimitiveIfNecessary(parameterType);
        Optional<StringToObjectMapper<?>> stringToObjectMapper = mapperContainer.get(parameterType);
        if (stringToObjectMapper.isPresent()) {
            return stringToObjectMapper.get().mapToObject(group);
        } else {
            String message = String.format("Unsupported annotated as @RegexGroup parameter type: %s. You should " +
                            "implement StringToObjectMapper with this parameter type and add it to spring context via " +
                            "@Component, @Bean or any other way",
                    parameterType.getSimpleName());
            throw new IllegalArgumentException(message);
        }
    }

    private void validateGroupNumber(int groupNumber, Matcher matcher) {
        if (groupNumber > matcher.groupCount()) {
            String message = String.format("There is no group with number %d", groupNumber);
            throw new IllegalArgumentException(message);
        }
    }
}
