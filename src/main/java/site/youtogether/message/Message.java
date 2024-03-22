package site.youtogether.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Message {

	private final String roomId;
	private final String username;
	private final String content;
	private final MessageType messageType;

}
