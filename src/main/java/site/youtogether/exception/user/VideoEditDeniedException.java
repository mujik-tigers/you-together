package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class VideoEditDeniedException extends CustomException {

	public VideoEditDeniedException() {
		super(ErrorType.VIDEO_EDIT_DENIED);
	}

}
