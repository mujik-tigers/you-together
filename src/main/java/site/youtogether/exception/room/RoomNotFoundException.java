package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class RoomNotFoundException extends CustomException {

	public RoomNotFoundException() {
		super(ErrorType.ROOM_NOT_FOUND);
	}

}
