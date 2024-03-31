package site.youtogether.message.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.RedisPublisher;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.AppConstants;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final RedisPublisher redisPublisher;
	private final UserStorage userStorage;

	@MessageMapping("/chat/message")
	public void handle(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		String sessionCode = (String)headerAccessor.getSessionAttributes().get(AppConstants.SESSION_CODE);
		String username = getUserNickname(sessionCode);
		chatMessage.setUsername(username);

		redisPublisher.publishMessage(chatMessage);
	}

	private String getUserNickname(String sessionCode) {
		return userStorage.findById(sessionCode)
			.map(User::getNickname)
			.orElseThrow(UserNoExistenceException::new);
	}

}
