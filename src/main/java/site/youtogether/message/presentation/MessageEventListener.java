package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.RedisPublisher;
import site.youtogether.room.application.RoomService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Component
@RequiredArgsConstructor
public class MessageEventListener {

	private final RedisPublisher redisPublisher;
	private final UserStorage userStorage;
	private final RoomService roomService;

	@EventListener
	public void handleWebSocketSubscriberListener(SessionSubscribeEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String simpDestination = event.getMessage().getHeaders().get("simpDestination").toString();
		String roomCode = simpDestination.substring(simpDestination.lastIndexOf("/") + 1);
		headerAccessor.getSessionAttributes().put(ROOM_CODE, roomCode);

		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		redisPublisher.publishParticipantsInfo(roomCode);
		redisPublisher.publishChat(new ChatMessage(roomCode, null, "[알림]", user.getNickname() + "님이 입장하셨습니다."));
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String roomCode = (String)headerAccessor.getSessionAttributes().get(ROOM_CODE);
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		roomService.leave(roomCode, userId);
		redisPublisher.publishParticipantsInfo(roomCode);
		redisPublisher.publishChat(new ChatMessage(roomCode, null, "[알림]", user.getNickname() + "님이 퇴장하셨습니다."));
	}

}
