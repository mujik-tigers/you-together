package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class UserNotInRoomException extends CustomException {

	public UserNotInRoomException() {
		super(ErrorType.USER_NOT_IN_ROOM);
	}

}
