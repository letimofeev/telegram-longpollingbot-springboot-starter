package org.telegram.telegrambot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.annotation.UpdateMapping;
import org.telegram.telegrambot.container.StringToObjectMapperContainer;
import org.telegram.telegrambot.container.UpdateMappingMethodContainer;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.expection.StringToObjectMapperException;
import org.telegram.telegrambot.repository.BotStateSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.telegram.telegrambot.repository.BotState.ANY_STATE;

@Component
public class UpdateMappingMethodProviderImpl implements UpdateMappingMethodProvider {

    private static final Logger log = LoggerFactory.getLogger(UpdateMappingMethodProviderImpl.class);

    private final BotStateSource botStateSource;
    private final UpdateMappingMethodContainer mappingMethodContainer;
    private final StringToObjectMapperContainer stringToObjectMapperContainer;

    public UpdateMappingMethodProviderImpl(BotStateSource botStateSource,
                                           UpdateMappingMethodContainer mappingMethodContainer,
                                           StringToObjectMapperContainer stringToObjectMapperContainer) {
        this.botStateSource = botStateSource;
        this.mappingMethodContainer = mappingMethodContainer;
        this.stringToObjectMapperContainer = stringToObjectMapperContainer;
    }

    @Override
    public Optional<InvocationUnit> getUpdateMappingMethod(Update update) {
        long chatId = update.getMessage().getChatId();
        String state = botStateSource.getState(chatId);

        log.debug("Getting mapping method for state: \"{}\" and update: {}", state, update);

        String message = update.getMessage().getText();
        if (message != null) {
            return getMessageMatchingInvocationUnit(update, state, message);
        }
        return Optional.empty();
    }

    private Optional<InvocationUnit> getMessageMatchingInvocationUnit(Update update, String state, String message) {
        return mappingMethodContainer.get(ANY_STATE)
                .flatMap(methodTargetPairs -> getMessageMatchingInvocationUnit(update, message, methodTargetPairs))
                .or(() -> mappingMethodContainer.get(state)
                        .flatMap(methodTargetPairs -> getMessageMatchingInvocationUnit(update, message, methodTargetPairs)));
    }

    private Optional<InvocationUnit> getMessageMatchingInvocationUnit(Update update, String message, List<MethodTargetPair> storedMappingMethods) {
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
            addTypedArgumentFromRegexGroup(matcher, args, parameter);
        }
        return args;
    }

    private void addTypedArgumentFromRegexGroup(Matcher matcher, List<Object> args, Parameter parameter) {
        RegexGroup annotation = parameter.getAnnotation(RegexGroup.class);
        if (annotation != null) {
            int groupNumber = annotation.value();
            validateGroupNumber(groupNumber, matcher);
            Class<?> parameterType = parameter.getType();
            String group = matcher.group(groupNumber);
            try {
                Object arg = getTypedArgumentFromRegexGroup(group, parameterType);
                args.add(arg);
            } catch (Exception e) {
                String message = String.format("Exception during mapping regex group [\"%s\"] to parameter [%s]; " +
                        "nested exception: %s", group, parameter, e);
                throw new StringToObjectMapperException(message, e);
            }
        }
    }

    private Object getTypedArgumentFromRegexGroup(String group, Class<?> parameterType) {
        parameterType = ClassUtils.resolvePrimitiveIfNecessary(parameterType);
        String parameterName = parameterType.getSimpleName();
        return stringToObjectMapperContainer.get(parameterType)
                .map(stringToObjectMapper -> stringToObjectMapper.mapToObject(group))
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Unsupported annotated as @RegexGroup parameter type: %s. You should " +
                                "implement StringToObjectMapper with this parameter type and add it to spring context via " +
                                "@Component, @Bean or any other way", parameterName)));
    }

    private void validateGroupNumber(int groupNumber, Matcher matcher) {
        if (groupNumber > matcher.groupCount()) {
            String message = String.format("There is no group with number %d", groupNumber);
            throw new IllegalArgumentException(message);
        }
    }
}
