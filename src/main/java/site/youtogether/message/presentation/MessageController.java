package site.youtogether.message.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.RedisPublisher;
import site.youtogether.util.AppConstants;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final RedisPublisher redisPublisher;

	@MessageMapping("/chat/message")
	public void handle(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		String nickname = (String)headerAccessor.getSessionAttributes().get(AppConstants.STOMP_SESSION_NICKNAME);
		chatMessage.setUsername(nickname);
		redisPublisher.publishMessage(chatMessage);
	}

}
