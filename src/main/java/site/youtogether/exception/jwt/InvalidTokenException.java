package site.youtogether.exception.jwt;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class InvalidTokenException extends CustomException {

	public InvalidTokenException() {
		super(ErrorType.INVALID_TOKEN);
	}

}
