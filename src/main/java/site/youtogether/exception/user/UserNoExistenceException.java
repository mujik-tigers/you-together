package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class UserNoExistenceException extends CustomException {

	public UserNoExistenceException() {
		super(ErrorType.USER_NO_EXISTENCE);
	}

}
