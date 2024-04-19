package site.youtogether.exception.jwt;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class AuthorizationHeaderNoExistenceException extends CustomException {

	public AuthorizationHeaderNoExistenceException() {
		super(ErrorType.AUTHORIZATION_HEADER_NO_EXISTENCE);
	}

}
