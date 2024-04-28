package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class PlaylistEmptyException extends CustomException {

	public PlaylistEmptyException() {
		super(ErrorType.PLAYLIST_EMPTY);
	}

}
