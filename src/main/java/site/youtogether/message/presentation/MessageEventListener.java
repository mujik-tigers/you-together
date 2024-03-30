package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.application.RedisPublisher;
import site.youtogether.user.infrastructure.UserStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

	private final UserStorage userStorage;
	private final RedisPublisher redisPublisher;

	@EventListener
	public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String simpDestination = event.getMessage().getHeaders().get("simpDestination").toString();
		String roomId = simpDestination.substring(simpDestination.lastIndexOf("/") + 1);
		headerAccessor.getSessionAttributes().put(STOMP_SESSION_ROOM_CODE, roomId);

		String username = (String)headerAccessor.getSessionAttributes().get(STOMP_SESSION_NICKNAME);

		redisPublisher.publishMessage(new ChatMessage(roomId, "[알림]", username + "님이 입장하셨습니다."));
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String roomId = (String)headerAccessor.getSessionAttributes().get(STOMP_SESSION_ROOM_CODE);
		String username = (String)headerAccessor.getSessionAttributes().get(STOMP_SESSION_NICKNAME);

		redisPublisher.publishMessage(new ChatMessage(roomId, "[알림]", username + "님이 퇴장하셨습니다."));
	}

}
