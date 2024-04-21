package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class UserAbsentException extends CustomException {

	public UserAbsentException() {
		super(ErrorType.USER_ABSENT);
	}

}
