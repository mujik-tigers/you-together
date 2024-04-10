package site.youtogether.message.application;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.ChatMessage;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	public void sendMessage(String publishMessage) {
		try {
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			messagingTemplate.convertAndSend("/sub/messages/rooms/" + chatMessage.getRoomCode(), chatMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
