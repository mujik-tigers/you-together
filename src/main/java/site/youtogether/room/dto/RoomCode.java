package site.youtogether.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.room.Room;

@AllArgsConstructor
@Getter
public class RoomCode {

	private final String roomCode;

	public RoomCode(Room room) {
		this.roomCode = room.getCode();
	}

}
