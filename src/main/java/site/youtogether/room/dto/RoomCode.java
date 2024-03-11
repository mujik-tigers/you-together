package site.youtogether.room.dto;

import lombok.Getter;
import site.youtogether.room.Room;

@Getter
public class RoomCode {

	private final String roomCode;

	public RoomCode(Room room) {
		this.roomCode = room.getCode();
	}

}
