package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class UserNicknameDuplicateException extends CustomException {

	public UserNicknameDuplicateException() {
		super(ErrorType.USER_NICKNAME_DUPLICATE);
	}

}
