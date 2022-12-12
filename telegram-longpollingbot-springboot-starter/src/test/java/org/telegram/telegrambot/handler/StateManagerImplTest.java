package org.telegram.telegrambot.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.telegram.telegrambot.annotation.NewBotState;
import org.telegram.telegrambot.repository.BotStateSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class StateManagerImplTest {

    @Mock
    private BotStateSource botStateSource;

    @InjectMocks
    private StateManagerImpl stateManager;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void setNewStateIfRequired_shouldCallSetStateMethod_whenNewStateSpecified() throws NoSuchMethodException {
        class MyUpdateHandler {
            @NewBotState("new state")
            public SendMessage handleMessage(Message message) {
                return null;
            }
        }
        long chatId = 19;
        Method method = MyUpdateHandler.class.getMethod("handleMessage", Message.class);

        stateManager.setNewStateIfRequired(chatId, method);

        verify(botStateSource, times(1)).setState(chatId, "new state");
    }

    @Test
    void setNewStateIfRequired_shouldNotCallSetStateMethod_whenNewStateNotSpecified() throws NoSuchMethodException {
        class MyUpdateHandler {
            public SendMessage handleMessage(Message message) {
                return null;
            }
        }
        long chatId = 20;
        Method method = MyUpdateHandler.class.getMethod("handleMessage", Message.class);

        stateManager.setNewStateIfRequired(chatId, method);

        verify(botStateSource, times(0)).setState(eq(chatId), anyString());
    }
}
