package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class HigherOrEqualRoleChangeException extends CustomException {

	public HigherOrEqualRoleChangeException() {
		super(ErrorType.HIGHER_OR_EQUAL_ROLE_CHANGE);
	}

}
