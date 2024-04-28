package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class PlaylistIndexOutOfBoundsException extends CustomException {

	public PlaylistIndexOutOfBoundsException() {
		super(ErrorType.PLAYLIST_INDEX_OUT_OF_BOUNDS);
	}

}
