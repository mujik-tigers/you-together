package site.youtogether.exception.cookie;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class SessionCookieNoExistenceException extends CustomException {

	public SessionCookieNoExistenceException() {
		super(ErrorType.SESSION_COOKIE_NO_EXISTENCE);
	}

}
