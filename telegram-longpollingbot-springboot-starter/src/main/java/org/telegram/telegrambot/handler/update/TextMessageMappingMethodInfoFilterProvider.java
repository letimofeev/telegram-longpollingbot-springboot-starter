package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.telegram.telegrambot.annotation.MessageMapping;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.function.Predicate;

@Component
public class TextMessageMappingMethodInfoFilterProvider implements UpdateMappingMethodInfoFilterProvider {

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Predicate<UpdateMappingMethodInfo> getFilter(Update update) {
        return mappingMethodInfo -> {
            if (!(update.hasMessage() && update.getMessage().hasText())) {
                return false;
            }
            String text = update.getMessage().getText();
            Method method = mappingMethodInfo.getMethod();
            MessageMapping annotation = method.getAnnotation(MessageMapping.class);
            if (annotation != null) {
                String message = annotation.message();
                if (message.equals(text)) {
                    return true;
                } else {
                    String pattern = annotation.messagePattern();
                    return matcher.match(pattern, text);
                }
            }
            return false;
        };
    }
}
