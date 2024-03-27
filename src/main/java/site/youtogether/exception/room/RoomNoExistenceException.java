package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class RoomNoExistenceException extends CustomException {

	public RoomNoExistenceException() {
		super(ErrorType.USER_NO_EXISTENCE);
	}

}
