package site.youtogether.room.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;

	public RoomCode create(String sessionCode, String address, RoomSettings roomSettings) {
		User host = User.builder()
			.sessionCode(sessionCode)
			.address(address)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.capacity(roomSettings.getCapacity())
			.title(roomSettings.getTitle())
			.password(roomSettings.getPassword())
			.host(host)
			.build();

		userStorage.save(host);
		roomStorage.save(room);

		return new RoomCode(room);
	}

	public void enter(String roomId, String ip) {
		User user = new User(ip);
		roomRepository.enter(roomId, user);
	}

	public List<Room> fetchAllRooms() {
		return roomRepository.findAll();
	}

}
