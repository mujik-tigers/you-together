package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.ChatMessageSendDeniedException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.application.RoomService;
import site.youtogether.user.User;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final RoomService roomService;
	private final MessageService messageService;

	@MessageMapping("/messages")
	public void handleMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		String roomCode = (String)headerAccessor.getSessionAttributes().get(ROOM_CODE);
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = roomService.findParticipant(roomCode, userId);

		if (user.isViewer()) {
			throw new ChatMessageSendDeniedException();
		}

		chatMessage.setUserId(user.getUserId());
		chatMessage.setNickname(user.getNickname());

		messageService.sendChat(chatMessage);
	}

}
