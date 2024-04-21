package site.youtogether.exception.cookie;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class CookieNoExistenceException extends CustomException {

	public CookieNoExistenceException() {
		super(ErrorType.COOKIE_NO_EXISTENCE);
	}

}
