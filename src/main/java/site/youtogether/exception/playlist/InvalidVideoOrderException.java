package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class InvalidVideoOrderException extends CustomException {

	public InvalidVideoOrderException() {
		super(ErrorType.INVALID_VIDEO_ORDER);
	}

}
