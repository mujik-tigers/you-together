package site.youtogether.message.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.Message;
import site.youtogether.message.MessageType;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final SimpMessageSendingOperations messagingTemplate;

	@MessageMapping("/chat/message")
	public void handle(Message message) {
		if (message.getMessageType().equals(MessageType.ENTER)) {
			message = new Message(message.getRoomId(), "[알림] ", message.getUsername() + "님이 입장하셨습니다.", MessageType.ENTER);
		}
		messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
	}

}
