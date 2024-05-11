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
import site.youtogether.message.AlarmMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.application.RoomService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

	private final UserStorage userStorage;
	private final RoomService roomService;
	private final MessageService messageService;

	@EventListener
	public void handleWebSocketSubscriberListener(SessionSubscribeEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String simpDestination = event.getMessage().getHeaders().get("simpDestination").toString();
		String roomCode = simpDestination.substring(simpDestination.lastIndexOf("/") + 1);
		headerAccessor.getSessionAttributes().put(ROOM_CODE, roomCode);

		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		log.info("--USER {} ROOM {} 웹 소켓 구독 시작--", userId, roomCode);

		messageService.sendParticipants(roomCode);
		messageService.sendPlaylist(roomCode);
		messageService.sendAlarm(new AlarmMessage(roomCode, "[알림] " + user.getNickname() + "님이 입장하셨습니다."));
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String roomCode = (String)headerAccessor.getSessionAttributes().get(ROOM_CODE);
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		log.info("--USER {} ROOM {} 웹 소켓 커넥션 종료 시도--", userId, roomCode);
		roomService.leave(userId);
		messageService.sendParticipants(roomCode);
		messageService.sendAlarm(new AlarmMessage(roomCode, "[알림] " + user.getNickname() + "님이 퇴장하셨습니다."));
	}

}
