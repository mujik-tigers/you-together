package site.youtogether.room.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;

@RequiredArgsConstructor
@Getter
public class ChangedRoomTitle {

	private final String roomCode;
	private final String changedRoomTitle;

	public ChangedRoomTitle(Room room) {
		this.roomCode = room.getCode();
		this.changedRoomTitle = room.getTitle();
	}

}
