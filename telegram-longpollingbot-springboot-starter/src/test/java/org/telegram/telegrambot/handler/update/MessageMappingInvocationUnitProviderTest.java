package org.telegram.telegrambot.handler.update;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.telegram.telegrambot.annotation.MessageMapping;
import org.telegram.telegrambot.annotation.RegexGroup;
import org.telegram.telegrambot.container.MessageMappingMethodContainer;
import org.telegram.telegrambot.container.StringToObjectMapperContainer;
import org.telegram.telegrambot.converter.StringToStringObjectMapper;
import org.telegram.telegrambot.dto.InvocationUnit;
import org.telegram.telegrambot.dto.MethodTargetPair;
import org.telegram.telegrambot.repository.BotStateSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.telegram.telegrambot.repository.BotState.ANY_STATE;

@TestInstance(PER_CLASS)
class MessageMappingInvocationUnitProviderTest {

    @Mock
    private BotStateSource botStateSource;

    @Spy
    private MessageMappingMethodContainer mappingMethodContainer = new MessageMappingMethodContainer();

    @Spy
    private StringToObjectMapperContainer stringToObjectMapperContainer = new StringToObjectMapperContainer(List.of(new StringToStringObjectMapper()));

    @InjectMocks
    private MessageMappingInvocationUnitProvider invocationUnitProvider;

    private AutoCloseable closeable;

    private static final Object messageHandler = new Object() {

        @MessageMapping(state = "state2")
        public SendMessage handleState2WithoutRegex(Message message) {
            return SendMessage.builder().text("text2").build();
        }

        @MessageMapping(state = "state1")
        public SendMessage handleState1WithoutRegex(Message message) {
            return SendMessage.builder().text("text1").build();
        }

        @MessageMapping(state = "state2", messageRegex = "Text: (.+)")
        public SendPhoto handleState2MatchingRegex(Message message, @RegexGroup(value = 1) String text) {
            return SendPhoto.builder().caption("caption3").build();
        }

        @MessageMapping(state = ANY_STATE, messageRegex = "Any with regex")
        public SendMessage handleAnyStateMatchingRegex(Message message) {
            return SendMessage.builder().text("any with regex").build();
        }
    };

    @BeforeAll
    void initContainers() {
        for (Method method : messageHandler.getClass().getDeclaredMethods()) {
            String state = method.getAnnotation(MessageMapping.class).state();
            mappingMethodContainer.putIfAbsent(state, new ArrayList<>());
            mappingMethodContainer.get(state).get().add(new MethodTargetPair(method, messageHandler));
        }
        closeable = openMocks(this);
    }

    @AfterAll
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getUpdateMappingInvocationUnit_shouldReturnMappingByState_whenMappingOnlyByState() throws NoSuchMethodException {
        long chatId = 1;
        Chat chat = new Chat();
        chat.setId(chatId);

        Message message = new Message();
        message.setChat(chat);
        message.setText("message123");

        when(botStateSource.getState(chatId)).thenReturn("state1");

        Method method = messageHandler.getClass().getMethod("handleState1WithoutRegex", Message.class);

        InvocationUnit expected = new InvocationUnit(new MethodTargetPair(method, messageHandler), new Object[]{message});
        Optional<InvocationUnit> actual = invocationUnitProvider.getUpdateMappingInvocationUnit(message);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void getUpdateMappingInvocationUnit_shouldReturnMappingWithRegex_whenMappingByStateAndMessageMatchesRegex() throws NoSuchMethodException {
        long chatId = 10;
        Chat chat = new Chat();
        chat.setId(chatId);

        Message message = new Message();
        message.setChat(chat);
        message.setText("Text: aaa))");

        when(botStateSource.getState(chatId)).thenReturn("state2");

        Method method = messageHandler.getClass().getMethod("handleState2MatchingRegex", Message.class, String.class);

        InvocationUnit expected = new InvocationUnit(new MethodTargetPair(method, messageHandler), new Object[]{message, "aaa))"});
        Optional<InvocationUnit> actual = invocationUnitProvider.getUpdateMappingInvocationUnit(message);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void getUpdateMappingInvocationUnit_shouldReturnAnyStateMapping_whenMessageMatchesRegexWithAnyState() throws NoSuchMethodException {
        long chatId = 11;
        Chat chat = new Chat();
        chat.setId(chatId);

        Message message = new Message();
        message.setChat(chat);
        message.setText("Any with regex");

        Method method = messageHandler.getClass().getMethod("handleAnyStateMatchingRegex", Message.class);

        InvocationUnit expected = new InvocationUnit(new MethodTargetPair(method, messageHandler), new Object[]{message});
        Optional<InvocationUnit> actual = invocationUnitProvider.getUpdateMappingInvocationUnit(message);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }
}