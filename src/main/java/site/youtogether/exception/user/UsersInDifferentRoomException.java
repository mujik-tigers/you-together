package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class UsersInDifferentRoomException extends CustomException {

	public UsersInDifferentRoomException() {
		super(ErrorType.USERS_IN_DIFFERENT_ROOM);
	}

}
