package site.youtogether.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChatMessage {

	private final MessageType messageType = MessageType.CHAT;

	private String roomId;
	private String username;
	private String content;

}
