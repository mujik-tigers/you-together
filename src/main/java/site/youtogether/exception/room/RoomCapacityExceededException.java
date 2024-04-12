package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class RoomCapacityExceededException extends CustomException {

	public RoomCapacityExceededException() {
		super(ErrorType.ROOM_CAPACITY_EXCEEDED);
	}

}
