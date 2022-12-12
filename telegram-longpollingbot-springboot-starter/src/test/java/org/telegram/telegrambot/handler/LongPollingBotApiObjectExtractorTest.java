package org.telegram.telegrambot.handler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LongPollingBotApiObjectExtractorTest {

    private final LongPollingBotApiObjectExtractor botApiObjectExtractor = new LongPollingBotApiObjectExtractor();

    @Test
    void extract_shouldReturnMessage_whenUpdateContainsMessage() {
        Update update = new Update();
        Message message = new Message();

        message.setText("text");
        update.setMessage(message);

        BotApiObject actual = botApiObjectExtractor.extract(update);

        assertEquals(message, actual);
    }

    @Test
    void extract_shouldThrowException_whenUpdateIsEmpty() {
        Update update = new Update();

        assertThrows(UnsupportedOperationException.class, () -> botApiObjectExtractor.extract(update));
    }
}
