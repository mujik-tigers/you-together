package site.youtogether.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

	private String roomId;
	private String username;
	private String content;
	private MessageType messageType;

}
