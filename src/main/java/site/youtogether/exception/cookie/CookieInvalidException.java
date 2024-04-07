package site.youtogether.exception.cookie;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class CookieInvalidException extends CustomException {

	public CookieInvalidException() {
		super(ErrorType.COOKIE_INVALID);
	}

}
