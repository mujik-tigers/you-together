package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class PlaylistNoExistenceException extends CustomException {

	public PlaylistNoExistenceException() {
		super(ErrorType.PLAYLIST_NO_EXISTENCE);
	}

}
