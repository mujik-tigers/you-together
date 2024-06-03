package site.youtogether.message;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatHistoriesMessage {

	private final MessageType messageType = MessageType.CHAT_HISTORIES;
	private final List<ChatHistory> chatHistories;

}

