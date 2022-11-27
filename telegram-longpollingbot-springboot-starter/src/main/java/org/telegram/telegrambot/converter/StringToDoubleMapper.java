package org.telegram.telegrambot.converter;

import org.springframework.stereotype.Component;

@Component
public class StringToDoubleMapper implements StringToObjectMapper<Double> {

    @Override
    public Double mapToObject(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
