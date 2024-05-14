package site.youtogether.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlarmMessage {

	private final MessageType messageType = MessageType.ALARM;

	private Long chatId;
	private String roomCode;
	private String content;
	private final LocalDateTime createdAt = LocalDateTime.now();

}
