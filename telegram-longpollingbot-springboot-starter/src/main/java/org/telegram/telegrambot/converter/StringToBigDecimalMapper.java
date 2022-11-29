package org.telegram.telegrambot.converter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StringToBigDecimalMapper implements StringToObjectMapper<BigDecimal> {

    @Override
    public BigDecimal mapToObject(String value) {
        return new BigDecimal(value);
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }
}
