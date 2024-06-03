package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class PlayingVideoNoExistenceException extends CustomException {

	public PlayingVideoNoExistenceException() {
		super(ErrorType.PLAYING_VIDEO_NO_EXISTENCE);
	}

}
