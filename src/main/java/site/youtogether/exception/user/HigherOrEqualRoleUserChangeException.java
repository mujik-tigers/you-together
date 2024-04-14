package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class HigherOrEqualRoleUserChangeException extends CustomException {

	public HigherOrEqualRoleUserChangeException() {
		super(ErrorType.HIGHER_OR_EQUAL_USER_ROLE_CHANGE);
	}

}
