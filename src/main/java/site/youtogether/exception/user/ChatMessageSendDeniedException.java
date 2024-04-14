package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class ChatMessageSendDeniedException extends CustomException {

	public ChatMessageSendDeniedException() {
		super(ErrorType.CHAT_MESSAGE_SEND_DENIED);
	}

}
