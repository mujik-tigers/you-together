package site.youtogether.exception.playlist;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class PlaylistLockAcquisitionFailureException extends CustomException {

	public PlaylistLockAcquisitionFailureException() {
		super(ErrorType.PLAYLIST_LOCK_ACQUISITION_FAIL);
	}

}
