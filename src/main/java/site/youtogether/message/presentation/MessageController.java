package site.youtogether.message.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.MessageType;
import site.youtogether.message.application.RedisPublisher;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final RedisPublisher redisPublisher;

	@MessageMapping("/chat/message")
	public void handle(ChatMessage chatMessage) {
		if (chatMessage.getMessageType().equals(MessageType.ENTER)) {
			chatMessage = new ChatMessage(chatMessage.getRoomId(), "[알림] ", chatMessage.getUsername() + "님이 입장하셨습니다.", MessageType.ENTER);
		}
		redisPublisher.publishMessage(chatMessage);
	}

}
