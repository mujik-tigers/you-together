package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.RedisPublisher;
import site.youtogether.room.application.RoomService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

	private final RedisPublisher redisPublisher;
	private final RoomService roomService;
	private final UserStorage userStorage;

	@EventListener
	public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		String roomCode = destination.substring(destination.lastIndexOf("/") + 1);
		headerAccessor.getSessionAttributes().put(ROOM_CODE, roomCode);

		String sessionCode = (String)headerAccessor.getSessionAttributes().get(SESSION_CODE);
		String nickname = getUserNickname(sessionCode);

		redisPublisher.publishParticipantsInfo(roomCode);
		redisPublisher.publishChatMessage(new ChatMessage(roomCode, "[알림]", nickname + "님이 입장하셨습니다."));
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String roomCode = (String)headerAccessor.getSessionAttributes().get(ROOM_CODE);
		String sessionCode = (String)headerAccessor.getSessionAttributes().get(SESSION_CODE);
		String nickname = getUserNickname(sessionCode);

		roomService.leave(roomCode, sessionCode);
		redisPublisher.publishParticipantsInfo(roomCode);
		redisPublisher.publishChatMessage(new ChatMessage(roomCode, "[알림]", nickname + "님이 퇴장하셨습니다."));
	}

	private String getUserNickname(String sessionCode) {
		return userStorage.findById(sessionCode)
			.map(User::getNickname)
			.orElseThrow(UserNoExistenceException::new);
	}

}
