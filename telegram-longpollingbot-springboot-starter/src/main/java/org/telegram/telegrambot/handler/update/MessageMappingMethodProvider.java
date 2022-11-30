package org.telegram.telegrambot.handler.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambot.annotation.MessageMapping;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.container.MessageMappingMethodContainer;
import org.telegram.telegrambot.container.StringToObjectMapperContainer;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.expection.StringToObjectMapperException;
import org.telegram.telegrambot.repository.BotStateSource;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.telegram.telegrambot.repository.BotState.ANY_STATE;

@Component
public class MessageMappingMethodProvider implements UpdateMappingMethodProvider<Message> {

    private static final Logger log = LoggerFactory.getLogger(MessageMappingMethodProvider.class);

    private final BotStateSource botStateSource;
    private final MessageMappingMethodContainer mappingMethodContainer;
    private final StringToObjectMapperContainer stringToObjectMapperContainer;

    public MessageMappingMethodProvider(BotStateSource botStateSource,
                                        MessageMappingMethodContainer mappingMethodContainer,
                                        StringToObjectMapperContainer stringToObjectMapperContainer) {
        this.botStateSource = botStateSource;
        this.mappingMethodContainer = mappingMethodContainer;
        this.stringToObjectMapperContainer = stringToObjectMapperContainer;
    }

    @Override
    public Optional<InvocationUnit> getUpdateMappingMethod(Message message) {
        long chatId = message.getChatId();
        String state = botStateSource.getState(chatId);

        log.debug("Getting mapping method for state: \"{}\" and message: {}", state, message);

        String text = message.getText();
        if (text != null) {
            return getMessageMatchingInvocationUnit(message, state, text);
        }
        return Optional.empty();
    }

    @Override
    public Class<Message> getUpdateType() {
        return Message.class;
    }

    private Optional<InvocationUnit> getMessageMatchingInvocationUnit(Message message, String state, String text) {
        return mappingMethodContainer.get(ANY_STATE)
                .flatMap(methodTargetPairs -> getMessageMatchingInvocationUnit(message, text, methodTargetPairs))
                .or(() -> mappingMethodContainer.get(state)
                        .flatMap(methodTargetPairs -> getMessageMatchingInvocationUnit(message, text, methodTargetPairs)));
    }

    private Optional<InvocationUnit> getMessageMatchingInvocationUnit(Message message, String text, List<MethodTargetPair> storedMappingMethods) {
        for (MethodTargetPair methodTargetPair : storedMappingMethods) {
            MessageMapping annotation = methodTargetPair.getMethod().getAnnotation(MessageMapping.class);
            String messageRegex = annotation.messageRegex();
            if (!messageRegex.isEmpty()) {
                Optional<InvocationUnit> mappingWithRegexMatching = getMappingWithRegexMatching(message, text, messageRegex, methodTargetPair);
                if (mappingWithRegexMatching.isPresent()) {
                    return mappingWithRegexMatching;
                }
            }
        }
        return getMappingWithoutRegexMatching(message, storedMappingMethods);
    }

    private Optional<InvocationUnit> getMappingWithRegexMatching(Message message, String text, String messageRegex,
                                                                 MethodTargetPair methodTargetPair) {
        Pattern pattern = Pattern.compile(messageRegex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            List<Object> args = getTypedRegexGroups(methodTargetPair.getMethod(), matcher);
            args.add(0, message);
            return Optional.of(new InvocationUnit(methodTargetPair, args.toArray()));
        }
        return Optional.empty();
    }

    private Optional<InvocationUnit> getMappingWithoutRegexMatching(Message message, List<MethodTargetPair> storedMappingMethods) {
        for (MethodTargetPair methodTargetPair : storedMappingMethods) {
            MessageMapping annotation = methodTargetPair.getMethod().getAnnotation(MessageMapping.class);
            String messageRegex = annotation.messageRegex();
            if (messageRegex.isEmpty()) {
                Object[] args = {message};
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
