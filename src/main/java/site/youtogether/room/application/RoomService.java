package site.youtogether.room.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomRepository;
import site.youtogether.user.User;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;

	public void enter(String roomId, String ip) {
		User user = new User(ip);
		roomRepository.enter(roomId, user);
	}

	public String createRoom(String ip, String name, int totalCapacity) {
		Room room = Room.builder()
			.name(name)
			.totalCapacity(totalCapacity)
			.build();
		roomRepository.save(room);
		enter(room.getRoomId(), ip);

		return room.getRoomId();
	}

	public List<Room> fetchAllRooms() {
		return roomRepository.findAll();
	}

}
