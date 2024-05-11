package site.youtogether.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlarmMessage {

	private final MessageType messageType = MessageType.ALARM;

	private String roomCode;
	private String content;
	private final String createdAt = LocalDateTime.now().toString();

}
