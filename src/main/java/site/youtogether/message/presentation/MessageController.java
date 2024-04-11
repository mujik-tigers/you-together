package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.RedisPublisher;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final UserStorage userStorage;
	private final RedisPublisher redisPublisher;

	@MessageMapping("/messages")
	public void handleMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		chatMessage.setUserId(user.getUserId());
		chatMessage.setNickname(user.getNickname());

		redisPublisher.publishChat(chatMessage);
	}

}
