package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class PasswordNotMatchException extends CustomException {

	public PasswordNotMatchException() {
		super(ErrorType.ROOM_PASSWORD_NOT_MATCH);
	}

}
