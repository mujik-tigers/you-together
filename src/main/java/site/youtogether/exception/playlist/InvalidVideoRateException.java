package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class InvalidVideoRateException extends CustomException {

	public InvalidVideoRateException() {
		super(ErrorType.INVALID_VIDEO_RATE);
	}

}
