package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class SelfRoleChangeException extends CustomException {

	public SelfRoleChangeException() {
		super(ErrorType.SELF_ROLE_CHANGE);
	}

}
