package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class InvalidVideoNumberException extends CustomException {

	public InvalidVideoNumberException() {
		super(ErrorType.INVALID_VIDEO_NUMBER);
	}

}
