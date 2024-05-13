package site.youtogether.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatHistory {

	private MessageType messageType;

	private Long userId;
	private String content;
	private String createdAt;

	public ChatHistory(ChatMessage chatMessage) {
		this.messageType = chatMessage.getMessageType();
		this.userId = chatMessage.getUserId();
		this.content = chatMessage.getContent();
		this.createdAt = chatMessage.getCreatedAt();
	}

	public ChatHistory(AlarmMessage alarmMessage) {
		this.messageType = alarmMessage.getMessageType();
		this.userId = null;
		this.content = alarmMessage.getContent();
		this.createdAt = alarmMessage.getCreatedAt();
	}

}
