package site.youtogether.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatMessage {

	private final MessageType messageType = MessageType.CHAT;

	private String roomCode;
	private Long userId;
	private String content;
	private final String createdAt = LocalDateTime.now().toString();

}
