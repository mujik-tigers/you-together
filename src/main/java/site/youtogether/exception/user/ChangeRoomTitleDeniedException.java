package site.youtogether.exception.user;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class ChangeRoomTitleDeniedException extends CustomException {

	public ChangeRoomTitleDeniedException() {
		super(ErrorType.ROOM_TITLE_CHANGE_DENIED);
	}

}
