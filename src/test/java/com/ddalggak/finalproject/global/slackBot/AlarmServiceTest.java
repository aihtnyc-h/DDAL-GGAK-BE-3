package com.ddalggak.finalproject.global.slackBot;

import com.slack.api.Slack;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

	@Mock
	private Slack slack;

	@InjectMocks
	private AlarmService alarmService;

	@BeforeEach
	void setUp() {
	}

	@Test
	void sendAlarmMessageTest() throws Exception {
		// Arrange
		String channelId = "C123456";
		String message = "Hello, World!";
		ChatPostMessageResponse response = mock(ChatPostMessageResponse.class);

		when(slack.methods(anyString()).chatPostMessage(any())).thenReturn(response);
		when(response.isOk()).thenReturn(true);

		// Act
		alarmService.sendAlarmMessage(channelId, message);

		// Assert
		verify(slack.methods(anyString()).chatPostMessage(any()), times(1)).isOk();
	}
}

