package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.ChatMessageSendDeniedException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final UserStorage userStorage;
	private final MessageService messageService;

	@MessageMapping("/messages")
	public void handleMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		if (user.isViewer()) {
			throw new ChatMessageSendDeniedException();
		}

		chatMessage.setUserId(user.getId());
		chatMessage.setNickname(user.getNickname());

		messageService.sendChat(chatMessage);
	}

}
