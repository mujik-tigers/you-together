package site.youtogether.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.room.Room;
import site.youtogether.user.User;

@AllArgsConstructor
@Getter
public class CreatedRoomInfo {

	private final String roomCode;
	private final String roomTitle;
	private final String hostNickname;
	private final int capacity;
	private final int currentParticipant;
	private final boolean passwordExist;

	public CreatedRoomInfo(Room room, User host) {
		this.roomCode = room.getCode();
		this.roomTitle = room.getTitle();
		this.hostNickname = host.getNickname();
		this.capacity = room.getCapacity();
		this.currentParticipant = 1;
		this.passwordExist = room.getPassword() != null;
	}

}
