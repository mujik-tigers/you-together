package site.youtogether.message;

import lombok.Getter;
import site.youtogether.room.Room;

@Getter
public class RoomTitleMessage {

	private final MessageType messageType = MessageType.ROOM_TITLE;

	private final String roomCode;
	private final String updatedTitle;

	public RoomTitleMessage(Room room) {
		this.roomCode = room.getCode();
		this.updatedTitle = room.getTitle();
	}

}
