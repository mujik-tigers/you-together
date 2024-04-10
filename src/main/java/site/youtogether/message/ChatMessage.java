package site.youtogether.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChatMessage {

	private final MessageType messageType = MessageType.CHAT;

	private String roomCode;
	private Long userId;
	private String nickname;
	private String content;

}
