package site.youtogether.room.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import site.youtogether.room.Room;
import site.youtogether.user.User;

@Repository
public class RoomRepository {

	private Map<String, Room> rooms = new HashMap<>();

	public void enter(String roomId, User user) {
		Room room = rooms.get(roomId);
		room.enter(user);
	}

	public void save(Room room) {
		rooms.put(room.getRoomId(), room);
	}

	public List<Room> findAll() {
		return new ArrayList<>(rooms.values());
	}

}
