package org.telegram.telegrambot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telegrambot")
public class TelegramBotProperties {

    private String username;
    private String token;
    private String initialState;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }
}
