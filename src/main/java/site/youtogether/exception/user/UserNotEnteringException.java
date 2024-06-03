package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class UserNotEnteringException extends CustomException {

	public UserNotEnteringException() {
		super(ErrorType.USER_NOT_ENTERING);
	}

}
