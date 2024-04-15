package site.youtogether.room.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;

@RequiredArgsConstructor
@Getter
public class UpdatedRoomTitle {

	private final String roomCode;
	private final String updatedRoomTitle;

	public UpdatedRoomTitle(Room room) {
		this.roomCode = room.getCode();
		this.updatedRoomTitle = room.getTitle();
	}

}
