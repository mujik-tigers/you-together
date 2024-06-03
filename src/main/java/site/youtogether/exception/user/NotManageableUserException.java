package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class NotManageableUserException extends CustomException {

	public NotManageableUserException() {
		super(ErrorType.NOT_MANAGEABLE);
	}

}
