package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class RoomNoExistenceException extends CustomException {

	public RoomNoExistenceException() {
		super(ErrorType.ROOM_NO_EXISTENCE);
	}

}
