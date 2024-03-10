package site.youtogether.exception.room;

import site.youtogether.exception.CustomException;
import site.youtogether.exception.ErrorType;

public class SingleRoomParticipationViolationException extends CustomException {

	public SingleRoomParticipationViolationException() {
		super(ErrorType.SINGLE_ROOM_PARTICIPATION_VIOLATION);
	}

}
