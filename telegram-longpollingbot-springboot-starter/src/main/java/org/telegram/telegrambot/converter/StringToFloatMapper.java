package org.telegram.telegrambot.converter;

import org.springframework.stereotype.Component;

@Component
public class StringToFloatMapper implements StringToObjectMapper<Float> {

    @Override
    public Float mapToObject(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }
}
