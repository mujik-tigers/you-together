package site.youtogether.room;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import site.youtogether.user.User;

@Getter
public class Room {

	private static final int ROOM_ID_LENGTH = 8;

	private final String roomId;
	private final String name;
	private final int totalCapacity;
	private final List<User> users = new ArrayList<>();

	@Builder
	private Room(String name, int totalCapacity) {
		this.roomId = UUID.randomUUID().toString().substring(0, ROOM_ID_LENGTH);
		this.name = name;
		this.totalCapacity = totalCapacity;
	}

	public void enter(User user) {
		users.add(user);
	}

}
